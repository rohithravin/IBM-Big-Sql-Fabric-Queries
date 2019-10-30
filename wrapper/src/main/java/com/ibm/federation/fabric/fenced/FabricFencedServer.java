/**
 *
 */
package com.ibm.federation.fabric.fenced;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.db2.wrapper.FencedGenericServer;
import com.ibm.db2.wrapper.FencedGenericWrapper;
import com.ibm.db2.wrapper.FencedRemoteUser;
import com.ibm.db2.wrapper.Nickname;
import com.ibm.db2.wrapper.RemoteConnection;
import com.ibm.db2.wrapper.RemoteUser;
import com.ibm.db2.wrapper.ServerInfo;
import com.ibm.federation.fabric.FabricRemoteConnection;
import com.ibm.federation.fabric.FabricWrapperLogging;

/**
 * @author obernin
 *
 */
public class FabricFencedServer extends FencedGenericServer {

	private static Logger logger = Logger.getLogger(FabricFencedServer.class.getName());

	/**
	 * @param name
	 * @param wrapper
	 */
	public FabricFencedServer(String name, FencedGenericWrapper wrapper) {
		super(name, wrapper);

		if (logger.isLoggable(Level.INFO))
			logger.info("Created FabricFencedServer " + name + " for FencedGenericWrapper " + wrapper + ": " + this);
	}

	// com.ibm.db2.wrapper.GenericFencedServer methods

	@Override
	protected RemoteUser createRemoteUser(String userName) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(userName));

		RemoteUser remoteUser = super.createRemoteUser(userName);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(remoteUser));

		return remoteUser;
	}

	// com.ibm.db2.wrapper.FencedServer methods

	@Override
	protected RemoteConnection createRemoteConnection(FencedRemoteUser arg0, int arg1, long arg2) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(arg0, arg1, arg2));

		RemoteConnection remoteConnection = new FabricRemoteConnection(this, arg0, arg1, arg2);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(remoteConnection));

		return remoteConnection;
	}

	@Override
	protected int getRemoteConnectionKind() {
		return RemoteConnection.NO_PHASE_KIND;
	}

	// com.ibm.db2.wrapper.Server methods

	@Override
	protected Nickname createNickname(String arg0, String arg1) throws Exception {

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.enterFormat(arg0, arg1));

		Nickname nickname = new FabricFencedNickname(arg0, arg1, this);

		if (logger.isLoggable(Level.FINEST))
			logger.finest(FabricWrapperLogging.exitFormat(nickname));

		return nickname;
	}

	@Override
	protected void destroy() throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Destroying FabricFencedServer " + this);

		super.destroy();
	}

	@Override
	protected void initializeMyServer(ServerInfo info) throws Exception {

		if (logger.isLoggable(Level.INFO))
			logger.info("Initializing FabricFencedServer " + this + " with " + info);

		super.initializeMyServer(info);
	}
}
