/**
 *
 */
package com.ibm.federation.fabric.fenced;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.db2.wrapper.NicknameInfo;
import com.ibm.db2.wrapper.WrapperException;
import com.ibm.federation.fabric.FabricWrapperLogging;
import com.ibm.db2.wrapper.CatalogOption;
import com.ibm.db2.wrapper.ColumnInfo;
import com.ibm.db2.wrapper.FencedGenericNickname;
import com.ibm.db2.wrapper.FencedGenericServer;

/**
 * @author obernin
 *
 */
public class FabricFencedNickname extends FencedGenericNickname {

	private final Object[][] SAMPLE_COLUMNS = {
		// NAME 		TYPE       	TYPE_SCHEMA	LENGTH, NULL
		{ "ID",		"INTEGER",	"SYSIBM",	null,	false },
		{ "NAME", 	"VARCHAR",	"SYSIBM",	8,		true	 },
		{ "VALUE",	"INTEGER",	"SYSIBM",	null,	false },
	};

	private static Logger logger = Logger.getLogger(FabricFencedNickname.class.getName());

	/**
	 * @param schema
	 * @param name
	 * @param server
	 */
	public FabricFencedNickname(String schema, String name, FencedGenericServer server) {
		super(schema, name, server);

		if (logger.isLoggable(Level.INFO))
			logger.info("Created FabricFencedNickname in schema " + schema + " with name " + name + " for server " + server + ": " + this);
	}

	// com.ibm.db2.wrapper.Nickname methods

	@Override
	protected void destroy() throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Destroying FabricFencedNickname " + this);

		super.destroy();
	}

	@Override
	protected void initializeMyNickname(NicknameInfo info) throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Initializing FabricFencedNickname " + this + " with " + info);

		super.initializeMyNickname(info);
	}

	@Override
	protected NicknameInfo verifyMyRegisterNicknameInfo(NicknameInfo nicknameInfo) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(nicknameInfo));

		if (logger.isLoggable(Level.INFO))
			logger.info("Verifying FabricFencedNickname " + this + " register info " + nicknameInfo);

		verifyNicknameInfo(nicknameInfo);

		NicknameInfo ni = new NicknameInfo();

		for (short index=0 ; index<SAMPLE_COLUMNS.length ; index++) {
			ColumnInfo column = new ColumnInfo();

			column.setColumnID(index);
			column.setColumnName((String) SAMPLE_COLUMNS[index][0]);
			column.setTypeName((String) SAMPLE_COLUMNS[index][1]);
			column.setTypeSchema((String) SAMPLE_COLUMNS[index][2]);
			if (SAMPLE_COLUMNS[index][3] != null)
				column.setOrgLength((Integer) SAMPLE_COLUMNS[index][3]);
			column.setNulls((Boolean) SAMPLE_COLUMNS[index][4]);

			ni.insertColumn(column);
		}

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(ni));

		return ni;
	}

	// Internal Methods

	private void verifyNicknameInfo(NicknameInfo nicknameInfo) throws WrapperException {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(nicknameInfo));

		CatalogOption option = nicknameInfo.getFirstOption();
		while (option != null) {

			if (logger.isLoggable(Level.CONFIG))
				logger.config("Option " + option.getName() + ": " + option.getValue());

			option = nicknameInfo.getNextOption(option);
		}

		if (logger.isLoggable(Level.CONFIG))
			logger.config(nicknameInfo.getNumColumns() + " column(s) specified");

		ColumnInfo column = nicknameInfo.getFirstColumn();
		while (column != null) {

			if (logger.isLoggable(Level.CONFIG))
				logger.config("Column " + column.getColumnType() + " " + column.getColumnName());

			column = nicknameInfo.getNextColumn(column);
		}

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat());
	}
}
