/**
 *
 */
package com.ibm.federation.fabric.unfenced;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.ibm.db2.wrapper.CatalogOption;
import com.ibm.db2.wrapper.Server;
import com.ibm.db2.wrapper.UnfencedGenericWrapper;
import com.ibm.db2.wrapper.WrapperException;
import com.ibm.db2.wrapper.WrapperInfo;
import com.ibm.db2.wrapper.WrapperUtilities;
import com.ibm.federation.fabric.FabricWrapperLogging;
import com.ibm.federation.fabric.fenced.FabricFencedWrapper;

/**
 * @author obernin
 *
 */
public class FabricUnfencedWrapper extends UnfencedGenericWrapper {

	private static Logger logger;

	static {
		try {
			InputStream ins = FabricUnfencedWrapper.class.getClassLoader().getResourceAsStream("com/ibm/federation/fabric/default-logging.properties");
			LogManager.getLogManager().readConfiguration(ins);

		} catch (IOException ioex) {
			WrapperUtilities.traceException(0, "static", 1000, ioex);
		}

		logger = Logger.getLogger(FabricUnfencedWrapper.class.getName());
	}

	/**
	 *
	 */
	public FabricUnfencedWrapper() {
		super();

		logger.info("Created FabricUnfencedWrapper instance");
		WrapperUtilities.traceFunctionData(0, "FabricUnfencedWrapper", 1000, "FabricUnfencedWrapper created");
	}

	// UnfencedWrapper Methods

	@Override
	protected WrapperInfo verifyMyAlterWrapperInfo(WrapperInfo wrapperInfo) throws WrapperException {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(wrapperInfo));

		WrapperInfo info = verifyFabricWrapperInfo(wrapperInfo);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(info));

		return info;
	}

	@Override
	protected WrapperInfo verifyMyRegisterWrapperInfo(WrapperInfo wrapperInfo) throws WrapperException {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(wrapperInfo));

		verifyFabricWrapperInfo(wrapperInfo);

		wrapperInfo = new WrapperInfo();
		setFencedWrapperClass(wrapperInfo, FabricFencedWrapper.class.getName());

		// add WRAPPER_OPTION
		wrapperInfo.addOption("WRAPPER_OPTION", "wrapper_value", CatalogOption.ADD);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(wrapperInfo));

		return wrapperInfo;
	}

	// Wrapper Methods

	@Override
	protected Server createServer(String name) throws WrapperException {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(name));

		Server server = new FabricUnfencedServer(name, this);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(server));

		return server;
	}

	@Override
	protected void destroy() throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat());

		WrapperUtilities.traceFunctionEntry(4, "destroy");

		super.destroy();

		WrapperUtilities.traceFunctionReturnCode(4, "destroy", 0);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat());
	}

	@Override
	protected void initializeMyWrapper(WrapperInfo info) throws WrapperException {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(info));

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat());
	}

	// Internal Methods

	private WrapperInfo verifyFabricWrapperInfo(WrapperInfo wrapperInfo) throws WrapperException {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(wrapperInfo));

		CatalogOption option = wrapperInfo.getFirstOption();
		while (option != null) {

			if (logger.isLoggable(Level.CONFIG))
				logger.config("Option " + option.getName() + ": " + option.getValue());

			option = wrapperInfo.getNextOption(option);
		}

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(wrapperInfo));

		return wrapperInfo;
	}
}
