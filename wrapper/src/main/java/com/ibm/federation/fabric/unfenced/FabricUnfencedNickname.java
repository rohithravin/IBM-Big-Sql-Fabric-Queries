/**
 *
 */
package com.ibm.federation.fabric.unfenced;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.db2.wrapper.CatalogOption;
import com.ibm.db2.wrapper.ColumnInfo;
import com.ibm.db2.wrapper.NicknameInfo;
import com.ibm.db2.wrapper.UnfencedGenericNickname;
import com.ibm.db2.wrapper.UnfencedGenericServer;
import com.ibm.db2.wrapper.WrapperException;
import com.ibm.federation.fabric.FabricWrapperLogging;

/**
 * @author obernin
 *
 */
public class FabricUnfencedNickname extends UnfencedGenericNickname {

	private static Logger logger = Logger.getLogger(FabricUnfencedNickname.class.getName());

	/**
	 * @param schema
	 * @param name
	 * @param server
	 */
	public FabricUnfencedNickname(String schema, String name, UnfencedGenericServer server) {
		super(schema, name, server);

		if (logger.isLoggable(Level.INFO))
			logger.info("Created FabricUnfencedNickname in schema " + schema + " with name " + name + " for server " + server + ": " + this);
	}

	// com.ibm.db2.wrapper.UnfencedNickname methods

	@Override
	protected NicknameInfo verifyMyAlterNicknameInfo(NicknameInfo arg0) throws Exception {
		// TODO Auto-generated method stub
		return super.verifyMyAlterNicknameInfo(arg0);
	}

	// com.ibm.db2.wrapper.Nickname methods

	@Override
	protected void destroy() throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Destroying FabricUnfencedNickname " + this);

		super.destroy();
	}

	@Override
	protected void initializeMyNickname(NicknameInfo info) throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Initializing FabricUnfencedNickname " + this + " with " + info);

		// Nothing to do just yet
	}

	@Override
	protected NicknameInfo verifyMyRegisterNicknameInfo(NicknameInfo nicknameInfo) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(nicknameInfo));

		if (logger.isLoggable(Level.INFO))
			logger.info("Verifying FabricUnfencedNickname " + this + " register info " + nicknameInfo);

		verifyNicknameInfo(nicknameInfo);

		nicknameInfo = new NicknameInfo();

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(nicknameInfo));

		return nicknameInfo;
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
