/**
 *
 */
package com.ibm.federation.fabric;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.db2.wrapper.FencedRemoteUser;
import com.ibm.db2.wrapper.FencedServer;
import com.ibm.db2.wrapper.RemoteConnection;
import com.ibm.db2.wrapper.RemotePassthru;
import com.ibm.db2.wrapper.RemoteQuery;

/**
 * @author obernin
 *
 */
public class FabricRemoteConnection extends RemoteConnection {

	private static Logger logger = Logger.getLogger(FabricRemoteConnection.class.getName());

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public FabricRemoteConnection(FencedServer arg0, FencedRemoteUser arg1, int arg2, long arg3) {
		super(arg0, arg1, arg2, arg3);

		if (logger.isLoggable(Level.INFO))
			logger.info("Created FabricRemoteConnection ID #" + arg3 + " of kind " + arg2 + " for FencedServer " + arg0 + " with FencedRemoteUser " + arg1 + ": " + this);
	}

	@Override
	protected void commit() throws Exception {

		if (logger.isLoggable(Level.FINE))
			logger.fine("Called commit() on FabricRemoteConnection " + this);

		super.commit();
	}

	@Override
	protected void connect() throws Exception {

		if (logger.isLoggable(Level.FINE))
			logger.fine("Called connect() on FabricRemoteConnection " + this);

		super.connect();
	}

	@Override
	protected RemotePassthru createRemotePassthru(long arg0) throws Exception {

		if (logger.isLoggable(Level.FINE))
			logger.fine("Called createRemotePassthru() on FabricRemoteConnection " + this);

		return super.createRemotePassthru(arg0);
	}

	@Override
	protected RemoteQuery createRemoteQuery(long arg0) throws Exception {

		if (logger.isLoggable(Level.FINE))
			logger.fine("Called createRemoteQuery() on FabricRemoteConnection " + this);

		return super.createRemoteQuery(arg0);
	}

	@Override
	protected void disconnect() throws Exception {

		if (logger.isLoggable(Level.FINE))
			logger.fine("Called disconnect() on FabricRemoteConnection " + this);

		super.disconnect();
	}

	@Override
	protected void rollback() throws Exception {

		if (logger.isLoggable(Level.FINE))
			logger.fine("Called rollback() on FabricRemoteConnection " + this);

		super.rollback();
	}
}
