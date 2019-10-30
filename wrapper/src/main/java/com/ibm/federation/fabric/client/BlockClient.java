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

import com.google.protobuf.InvalidProtocolBufferException;
import com.ibm.federation.fabric.network.FabricConnection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.SDKUtils;

/**
 * @author Rohith Ravindranath
 * @version June 29 2018
 * Wrapper class for org.hyperledger.fabric.sdk.BlockInfo
 */

public class BlockClient{
	
	private BlockInfo block;

	/**
	 * Constructor
	 * @param block BlockInfo
	 */
	public BlockClient(BlockInfo block) {
		this.block = block;
	}

	/**
	 * Getter method for private instance of BlockInfo
	 * @return BlockInfo instance
	 */
	public BlockInfo getBlock() {
		return this.block;
	}

	/**
	 * Getter method for the block number
	 * @return blockNumber long
	 */
	public long getBlockNumber() {
		return block.getBlockNumber();
	}

	/**
	 * Getter method for channel id
	 * @return channelID string
	 */
	public String getChannelId() {
		try {
			return block.getChannelId();
		} catch (InvalidProtocolBufferException e) {
			return null;
		}
	}

	/**
	 * Getter method for hash value
	 * @return hash byte[]
	 */
	public byte[] getHash(FabricConnection conn) {
		try {
			return SDKUtils.calculateBlockHash(conn.getHFChannelClient().getHFClient(), this.getBlockNumber(), this.getPreviousHash(), this.getDataHash());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Getter method for data hash value
	 * @return dataHash byte[]
	 */
	public byte[] getDataHash() {	
		return block.getDataHash();
	}

	/**
	 * Getter method for previous hash value
	 * @return previousHash byte[]
	 */
	public byte[] getPreviousHash() {
		return block.getPreviousHash();
	}

	/**
	 * Getter method for number of Envelopes
	 * @return numOfEnvelopes int
	 */
	public int getNumberOfEnvelopes() {
		return block.getEnvelopeCount();
	}

	/**
	 * Getter method for list of Envelopes
	 * @return listOfEnvelopes List<EnvelopeInfoClient>
	 */
	public List<EnvelopeInfoClient> getEnvelopeInfos(){
		List<EnvelopeInfoClient> list = new ArrayList<EnvelopeInfoClient>();
		for(int x = 0; x < this.block.getEnvelopeCount(); x++) {
			try {
				list.add(new EnvelopeInfoClient(block.getEnvelopeInfo(x)));
			} catch (InvalidProtocolBufferException e) {
				return null;
			}
		}
		return list;
	}

	/**
	 * Getter method for list of Envelopes
	 * @return listOfEnvelopes List<EnvelopeInfoClient>
	 */
	public int getNumberOfTransactions() {
		return block.getTransactionCount();
	}

	/**
	 * Getter method for list of transaction ids
	 * @return transactionIds List<String>
	 */
	public List<String> getTransactionIds(){
		List<String> list = new ArrayList<String>();
		for(int x = 0; x < this.getNumberOfTransactions(); x++) {
			try {
				list.add(block.getEnvelopeInfo(block.getEnvelopeCount()-1).getTransactionID());
			} catch (InvalidProtocolBufferException e) {
				return null;
			}
		}
		return list;
	}

	/**
	 * Getter method for transaction id within block instance
	 * @return transactionId String
	 */
	public String getTransactionId(int num) {	
		try {
			return block.getEnvelopeInfo(num).getTransactionID();
		} catch (InvalidProtocolBufferException e) {
			return null;
		}
	}

	/**
	 * Getter method for time stamp of a transaction within the block instance
	 * @return timeStamp Date
	 */
	public Date timeStampOfTransaction(int num) {
		try {
			return block.getEnvelopeInfo(block.getEnvelopeCount()-1).getTimestamp();
		} catch (InvalidProtocolBufferException e) {
			return null;
		}
	}

	/**
	 * Utility method that converts encoded byte[] to string
	 * @return hashValue String
	 */
	public String hashToString(byte [] hash) {
		return Hex.encodeHexString(hash);
	}
	
}