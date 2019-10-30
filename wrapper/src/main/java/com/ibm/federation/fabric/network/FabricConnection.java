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
package com.ibm.federation.fabric.network;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import com.ibm.federation.fabric.client.BlockClient;
import com.ibm.federation.fabric.user.FabricUser;
import com.ibm.federation.fabric.client.HFChannelClient;
import com.ibm.federation.fabric.client.CertificateAuthClient;

import com.ibm.federation.fabric.utils.Utilities;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

/**
 * @author Rohith Ravindranath
 * @version June 29 2018
 * Class that manages and establishes the connection to the blockchain network
 */
public class FabricConnection{
	
	private HFChannelClient channelClient;

	/**
	 * Constructor
	 * @param userAffiliation

	 */

	/*
	 public FabricConnection(String userName, String userAffiliation, String userMspId, String userCertFilePath, String userPkFilePath,String channelName, String peerName, String peerUrl, String ordererName, String ordererUrl) throws Exception {
		 FabricUser user = new FabricUser(userName, userAffiliation, userMspId, userCertFilePath, userPkFilePath);
		 this.createChannel(channelName, peerName,peerUrl, ordererName, ordererUrl, user);
	 }
	*/

	 public FabricConnection(String userName,String password, String userAffiliation, String userMspId,String channelName, String peerName, String peerUrl, String caURL,String ordererName, String ordererUrl) throws Exception {
	 	Utilities.cleanUp();
	 	FabricUser user = new FabricUser(userName, userAffiliation, userMspId);
	 	System.out.println(caURL);
		CertificateAuthClient caClient = new CertificateAuthClient(caURL, null);
		 System.out.println(caURL);
		caClient.setAdminFabricUser(user);
		user = caClient.enrollAdminUser(userName, password);
		this.createChannel(channelName, peerName,peerUrl, ordererName, ordererUrl, user);
	 }

	public FabricConnection(String userName, String userAffiliation, String userMspId,String channelName, String peerName ,String peerUrl,String caURL, String ordererName, String ordererUrl) throws Exception {
		Utilities.cleanUp();
	 	FabricUser user = new FabricUser(userName, userAffiliation, userMspId);
		CertificateAuthClient caClient = new CertificateAuthClient(caURL, null);
		String eSecret = caClient.registerUser(userName, userAffiliation);
		user = caClient.enrollUser(user, eSecret);
		this.createChannel(channelName, peerName,peerUrl, ordererName, ordererUrl, user);
	}

	 private void createChannel(String channelName, String peerName, String peerUrl, String ordererName, String ordererUrl, FabricUser user) throws NoSuchMethodException, InvocationTargetException, InvalidArgumentException, InstantiationException, IllegalAccessException, CryptoException, ClassNotFoundException, TransactionException {
		 this.channelClient = new HFChannelClient(user,channelName);
		 Channel channel = channelClient.getChannel();
		 Peer peer = channelClient.getHFClient().newPeer(peerName, peerUrl);
		 Orderer orderer = channelClient.getHFClient().newOrderer(ordererName, ordererUrl);
		 channel.addPeer(peer);
		 channel.addOrderer(orderer);
		 channel.initialize();
	 }

	/**
	 * Getter method for the HFChannel instance
	 * @return channelClient HFChannelClient
	 */
	 public HFChannelClient getHFChannelClient() {
		 return this.channelClient;
	 }

	/**
	 * Method that queries the blockchain chaincode and returns a value.
	 * Method will only work if the function name exists in the chaincode,
	 * else use sendTransactionProposal() from HFChannelClient
	 * @param arguments
	 * @param chaincodeFunction
	 * @return responses Collection<ProposalResponse>
	 */
	public Collection<ProposalResponse> queryBlockChain( String chaincode, String chaincodeFunction, String [] arguments) throws Exception{
		Collection<ProposalResponse>  responsesQuery = this.getHFChannelClient().queryByChainCode(chaincode, chaincodeFunction, arguments);
		return responsesQuery;
	}

	/**
	 * Method that gets the summary information about the blockchain specific to the channel you are connected to
	 * @return block BlockchainInfo
	 */
	public BlockchainInfo getBlockChainInfo() throws ProposalException, InvalidArgumentException {
		BlockchainInfo channelInfo = this.getHFChannelClient().getChannel().queryBlockchainInfo();
		return channelInfo;
	}

	/**
	 * Method that queries a range of blocks from the given connection
	 * @param end_num
	 * @param start_num
	 * @return blocks Collection<BlockClient>
	 */
	public Collection<BlockClient> queryBlockByRange( long start_num , long end_num) throws InvalidArgumentException, ProposalException {
		Collection<BlockClient> list = new ArrayList<>();
		BlockchainInfo info = this.getBlockChainInfo();
		if(end_num > info.getHeight()){
			end_num = info.getHeight();
		}
		for(int x = (int)start_num; x < end_num; x++){
			list.add(queryByBlockNumber(x));
		}
		return list;
	}

	/**
	 * Method that queries a specific block by the block number. If block doesn't exist will return null
	 * @param number
	 * @return block BlockClient
	 */
	public BlockClient queryByBlockNumber( long number) throws InvalidArgumentException, ProposalException {
		BlockchainInfo info = this.getBlockChainInfo();
		if(number >= info.getHeight()){
			number = info.getHeight() - 1;
		}
		BlockInfo block = this.getHFChannelClient().getChannel().queryBlockByNumber(number);
		BlockClient blockclient = new BlockClient(block);
		return blockclient;
	}

	/**
	 * Method that queries a specific block by the hash value. If block doesn't exist will return null
	 * @param hash
	 * @return block BlockClient
	 */
	public BlockClient queryByBlockHash( byte [] hash) throws InvalidArgumentException, ProposalException {
		BlockInfo block = this.getHFChannelClient().getChannel().queryBlockByHash(hash);
		BlockClient blockclient = new BlockClient(block);
		return blockclient;
	}

	/**
	 * Method that queries a specific block by the transaction id. If block doesn't exist will return null
	 * @param txnId
	 * @return block BlockClient
	 */
	public BlockClient queryBlockByTransactionID( String txnId) throws InvalidArgumentException, ProposalException {
		BlockInfo block = this.getHFChannelClient().getChannel().queryBlockByTransactionID(txnId);
		BlockClient blockclient = new BlockClient(block);
		return blockclient;
	}

	/**
	 * Method that queries a specific transaction by the transaction id. If block doesn't exist will return null
	 * @param txnId
	 * @return block BlockClient
	 */
	public TransactionInfo queryByTransactionId( String txnId) throws Exception {
		return this.getHFChannelClient().queryByTransactionId(txnId);

	}


	/**
	 * Method to close the connection to the blockchain
	 */
	 public void close() {
		 this.channelClient = null;
		 Utilities.cleanUp();
	 }
}