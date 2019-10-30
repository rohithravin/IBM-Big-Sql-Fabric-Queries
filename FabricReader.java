package com.ibm.biginsights.bigsql.dfsrw.reader; //NEED TO EDIT AND CHANCE THIS

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.metadata.HiveStorageHandler;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.plan.TableScanDesc;
import org.apache.hadoop.hive.serde2.ColumnProjectionUtils;
import org.apache.hadoop.hive.serde2.Deserializer;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;

import com.ibm.biginsights.bigsql.BigSqlLogging;
import com.ibm.biginsights.bigsql.dfsrw.DfsException;
import com.ibm.biginsights.bigsql.dfsrw.DfsException.ExceptionKind;
import com.ibm.biginsights.bigsql.dfsrw.DfsMetricsProvider;
import com.ibm.biginsights.bigsql.dfsrw.DfsProperties;
import com.ibm.biginsights.bigsql.dfsrw.jaro.DfsJobManager;
import com.ibm.biginsights.bigsql.dfsrw.jaro.DfsScan;
import com.ibm.biginsights.bigsql.dfsrw.reader.avro.DfsAvroReader;
import com.ibm.biginsights.bigsql.dfsrw.reader.parquet.DfsParquetReader;
import com.ibm.biginsights.bigsql.dfsrw.reader.rcfile.DfsRCFileReader;
import com.ibm.biginsights.bigsql.dfsrw.reader.text.DfsTextReader;
import com.ibm.biginsights.bigsql.dfsrw.scheduler.DfsSchedulerProxy;
import com.ibm.biginsights.bigsql.dfsrw.spark.SparkListener;
import com.ibm.biginsights.bigsql.dfsrw.stats.AnalyzeDfsHiveParquetReader;
import com.ibm.biginsights.bigsql.dfsrw.stats.AnalyzeDfsHiveReader;
import com.ibm.biginsights.bigsql.dfsrw.utilities.DfsClassLoader;
import com.ibm.biginsights.bigsql.dfsrw.utilities.HadoopUtilities;
import com.ibm.biginsights.bigsql.dfsrw.utilities.HiveUtilities;
import com.ibm.biginsights.bigsql.hbasecommon.util.BigSqlHBaseTableUtil;
import com.ibm.biginsights.bigsql.io.hbase.BigSqlHBaseInputFormat;
import com.ibm.biginsights.bigsql.pushdown.PushdownUtils;
import com.ibm.biginsights.bigsql.scheduler.Utils;
import com.ibm.biginsights.bigsql.spark.SparkConstants;
import com.ibm.biginsights.bigsql.stats.AnalyzeUtil;
import com.thirdparty.cimp.util.BigSqlUtils;

import COM.ibm.db2.app.PredExternal;
import COM.ibm.db2.app.SqlExternalColRef;
import COM.ibm.db2.app.SqlReader;
import COM.ibm.db2.app.SqlReaderContext;
import COM.ibm.db2.app.SqlReaderContext.Metrics;

public class FabricReader implements SqlReader {

  // begin ridiculous copyright
  @SuppressWarnings ("unused")
  private static final String _c = "Licensed Materials - Property of IBM "
      + "(C) Copyright IBM Corp. 2010, 2018 US Government Users Restricted "
      + "Rights - Use, duplication  disclosure restricted by GSA ADP Schedule "
      + "Contract with IBM Corp.";
  // end ridiculous copyright

  public static final Logger LOG = Logger.getLogger(FabricReader.class);
  public static final boolean logging = LOG.isInfoEnabled();
  public static final boolean isTrace = LOG.isTraceEnabled();
  

  private static final Constructor<?> orcConstructor, aOrcConstructor;
  static {
    Constructor<?> constructor = null;
    Constructor<?> aConstructor = null;

    // First we make sure we have an ORC ClasLoader - Unit tests need this as they won't have the jar sought below
    ClassLoader orcClassLoader = FabricReader.class.getClassLoader();
    try {
      final String orcJarsDir = System.getenv("BIGSQL_HOME") + File.separator + "orc" + File.separator;
      // Only load jar files
      File[] files = new File(orcJarsDir).listFiles((File dir, String name)->name.toLowerCase().endsWith(".jar"));
      if ((files != null) && (files.length > 0)) { // Directory is good, no I/O error and jar file(s) exist
        final URL[] urlArray = new URL[files.length];
        int i = 0;
        for (File file : files) {
          urlArray[i++] = new URL("file://" + file.getAbsolutePath());
        }
        orcClassLoader = new DfsClassLoader(urlArray);
        LOG.info("Created Fabric classloader with " + Arrays.toString(urlArray));
      } else {
        LOG.warn("Fabric jars not found under " + orcJarsDir);
      }

      final String className = "org.hyperledger.fabric.protos.msp.Identities.SerializedIdentity.getSerializedSize";
      final Class<?> depsClass = Class.forName(className, true, orcClassLoader);
      Constructor<?>[] constructors = depsClass.getConstructors();
      for (Constructor<?> construct : constructors) {
        if (construct.getParameterTypes().length == 1) {
          construct.setAccessible(true);
          constructor = construct;
          break;
        }
      }
    } catch (Exception e) {
      LOG.error("Failed to create Fabric class loaders / constructors", e);
    }

    orcConstructor = constructor;
    if (orcConstructor == null) {
      LOG.warn("No Fabric constructor found");
    }
    
    aOrcConstructor = aConstructor;
    if (aOrcConstructor == null) {
      LOG.warn("No Analyze Fabric constructor found");
    }
  }
