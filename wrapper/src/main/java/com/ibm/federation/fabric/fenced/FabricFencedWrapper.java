/**
 *
 */
package com.ibm.federation.fabric.fenced;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.db2.wrapper.CatalogOption;
import com.ibm.db2.wrapper.FencedGenericWrapper;
import com.ibm.db2.wrapper.Server;
import com.ibm.db2.wrapper.WrapperException;
import com.ibm.db2.wrapper.WrapperInfo;
import com.ibm.db2.wrapper.WrapperUtilities;
import com.ibm.federation.fabric.FabricWrapperLogging;

/**
 * @author obernin
 *
 */
public class FabricFencedWrapper extends FencedGenericWrapper {

	private final static Logger logger = Logger.getLogger(FabricFencedWrapper.class.getName());

	/**
	 *
	 */
	public FabricFencedWrapper() {

		logger.info("Created FabricFencedWrapper instance");
		WrapperUtilities.traceFunctionData(7, "FabricFencedWrapper", 1000, "FabricFencedWrapper created");
	}

	// Wrapper Methods

	@Override
	protected Server createServer(String name) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(name));

		Server server = new FabricFencedServer(name, this);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(server));

		return server;
	}

	@Override
	protected void destroy() throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat());

		WrapperUtilities.traceFunctionEntry(9, "destroy");

		super.destroy();

		WrapperUtilities.traceFunctionReturnCode(9, "destroy", 0);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat());
	}

	@Override
	protected void initializeMyWrapper(WrapperInfo wrapperInfo) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(wrapperInfo));

		verifyFabricWrapperInfo(wrapperInfo);

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
