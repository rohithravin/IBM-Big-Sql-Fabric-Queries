/*
 * Copyright 2018, 2018 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ibm.federation.fabric.client;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;


/**
 * @author Rohith Ravindranath
 * @version June 29 2018
 * Wrapper class that encapsulates the channel
 */

public class HFChannelClient {

	private Channel channel;
	private HFClient instance;

	public HFChannelClient(User context, String channelName) throws CryptoException, InvalidArgumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		instance = HFClient.createNewInstance();
		instance.setCryptoSuite(cryptoSuite);
		instance.setUserContext(context);
		this.channel = instance.newChannel(channelName);
	}

	public HFClient getHFClient(){return this.instance;}

	/**
	 * Getter method for channel instance
	 * @return channel Channel
	 */
	public Channel getChannel() {
		return this.channel;
	}

	/**
	 * Method with create a chaincode proposal to query information from the corresponding query
	 * function within the chaincode of this instance. functionName must exist in
	 * the chaincode and also return a value back. If it doesn't return anything back,
	 * use sendTransactionProposal().
	 * @param chaincode
	 * @param args
	 * @param functionName
	 * @return responses Collection<ProposalResponse>
	 */
	public Collection<ProposalResponse> queryByChainCode(String chaincode, String functionName, String[] args) throws Exception {
		QueryByChaincodeRequest request = this.instance.newQueryProposalRequest();
		ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincode).build();
		request.setChaincodeID(ccid);
		request.setFcn(functionName);
		if (args != null)
			request.setArgs(args);
		Collection<ProposalResponse> response = channel.queryByChaincode(request);
		return response;
	}

	/**
	 * Queries a specific transaction within the channel. Will send the txid to all the peers.
	 * Peers will check the blockchain and ledger and return the TransactionInfo.
	 * @return chaincode String
	 */
	public TransactionInfo queryByTransactionId(String txnId) throws Exception {
		Collection<Peer> peers = channel.getPeers();
		for (Peer peer : peers) {
			TransactionInfo info = channel.queryTransactionByID(peer, txnId);
			return info;
		}
		return null;
	}


	/**
	 * Method with create a chaincode proposal to send a tranasction to the chaincode
	 * by invoking the corresponding chaincode function. functionName must exist in the chaincode and must
	 * not return anything back. If it does return anything back, use queryByChainCode().
	 * @param chaincode
	 * @param arguments
	 * @param functionName
	 * @return responses Collection<ProposalResponse>
	 */
	public Collection<ProposalResponse> sendTransactionProposal( String chaincode, String functionName, String [] arguments)
			throws ProposalException, InvalidArgumentException {
		ChaincodeID ccid  = ChaincodeID.newBuilder().setName(chaincode).build();
		TransactionProposalRequest request = this.instance.newTransactionProposalRequest();
		request.setChaincodeID(ccid);
		request.setFcn(functionName);
		if (arguments != null)
			request.setArgs(arguments);
		request.setProposalWaitTime(1000);
		Collection<ProposalResponse> response = channel.sendTransactionProposal(request, channel.getPeers());
		channel.sendTransaction(response);
		return response;
	}
}
