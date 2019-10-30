/**
 *
 */
package com.ibm.federation.fabric.unfenced;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.db2.wrapper.CatalogOption;
import com.ibm.db2.wrapper.Nickname;
import com.ibm.db2.wrapper.PredicateList;
import com.ibm.db2.wrapper.RemoteFunctionInfo;
import com.ibm.db2.wrapper.RemoteUser;
import com.ibm.db2.wrapper.Reply;
import com.ibm.db2.wrapper.Request;
import com.ibm.db2.wrapper.ServerInfo;
import com.ibm.db2.wrapper.UnfencedGenericServer;
import com.ibm.db2.wrapper.UnfencedGenericWrapper;
import com.ibm.db2.wrapper.WrapperException;
import com.ibm.federation.fabric.FabricWrapperLogging;

/**
 * @author obernin
 *
 */
public class FabricUnfencedServer extends UnfencedGenericServer {

	private static Logger logger = Logger.getLogger(FabricUnfencedServer.class.getName());

	/**
	 * @param name
	 * @param wrapper
	 */
	public FabricUnfencedServer(String name, UnfencedGenericWrapper wrapper) {
		super(name, wrapper);

		if (logger.isLoggable(Level.INFO))
			logger.info("Created FabricUnfencedServer " + name + " for UnfencedGenericWrapper " + wrapper + ": " + this);
	}

	// com.ibm.db2.wrapper.UnfencedGenericServer methods

	@Override
	protected RemoteUser createRemoteUser(String userName) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(userName));

		RemoteUser remoteUser = super.createRemoteUser(userName);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(remoteUser));

		return remoteUser;
	}

	@Override
	public float getSelectivity(PredicateList predicateList) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(predicateList));

		float selectivity = super.getSelectivity(predicateList);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(selectivity));

		return selectivity;
	}

	@Override
	protected Reply planRequest(Request request) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(request));

		Reply reply = this.createReply(request);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(reply));

		return reply;
	}

	// com.ibm.db2.wrapper.UnfencedServer methods

	@Override
	protected void setMyDefaultRemoteFunctionMappings() throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat());

		super.setMyDefaultRemoteFunctionMappings();

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat());
	}

	@Override
	protected ServerInfo verifyMyAlterServerInfo(ServerInfo arg0) throws Exception {
		// TODO Auto-generated method stub
		return super.verifyMyAlterServerInfo(arg0);
	}

	@Override
	protected void verifyMyFunctionMappingInfo(RemoteFunctionInfo info) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(info));

		super.verifyMyFunctionMappingInfo(info);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat());
	}

	@Override
	protected ServerInfo verifyMyRegisterServerInfo(ServerInfo info) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(info));

		ServerInfo serverInfo = verifyFabricServerInfo(info);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(serverInfo));

		return serverInfo;
	}

	// com.ibm.db2.wrapper.Server methods

	@Override
	protected Nickname createNickname(String arg0, String arg1) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(arg0, arg1));

		Nickname nickname = new FabricUnfencedNickname(arg0, arg1, this);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(nickname));

		return nickname;
	}

	@Override
	protected void destroy() throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Destroying FabricUnfencedServer " + this);

		super.destroy();
	}

	@Override
	protected void initializeMyServer(ServerInfo info) throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Initializing FabricUnfencedServer " + this + " with " + info);

		super.initializeMyServer(info);
	}

	// Internal Methods

	private ServerInfo verifyFabricServerInfo(ServerInfo serverInfo) throws WrapperException {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(serverInfo));

		CatalogOption option = serverInfo.getFirstOption();
		while (option != null) {

			if (logger.isLoggable(Level.CONFIG))
				logger.config("Option " + option.getName() + ": " + option.getValue());

			option = serverInfo.getNextOption(option);
		}

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(serverInfo));

		return serverInfo;
	}

	private void logRequest(Level level, Request request) {

		logger.log(level, request.getNumberOfHeadExp() + " head expression(s)");
		for (int i=0 ; i<request.getNumberOfHeadExp() ; i++) {
			request.getHeadExp(i);
		}
	}
}
