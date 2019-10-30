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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hyperledger.fabric.sdk.BlockInfo.EnvelopeInfo;
import org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType;
import org.hyperledger.fabric.sdk.BlockInfo.TransactionEnvelopeInfo;

/**
 * @author Rohith Ravindranath
 * @version June 29 2018
 * Wrapper class for org.hyperledger.fabric.sdk.BlockInfo.EnvelopeInfo
 */
public class EnvelopeInfoClient{
	
	final EnvelopeInfo instance;

	/**
	 * Constructor
	 * @param instance EnvelopeInfo
	 */
	public EnvelopeInfoClient(EnvelopeInfo instance) {
		this.instance = instance;
	}

	/**
	 * Getter method for channelId of EnvelopeInfo instance
	 * @return channelId String
	 */
	public String getChannelID() {
		return this.instance.getChannelId();
	}

	/**
	 * Getter method for transactionID of EnvelopeInfo instance
	 * @return transactionId String
	 */
	public String getTransactionID() {

		return this.instance.getTransactionID();
	}

	/**
	 * Getter method for time stamp of transaction within EnvelopeInfo instance
	 * @return timeStamp String
	 */
	public Date getTransactionTimeStamp() {
		return this.instance.getTimestamp();
	}

	/**
	 * Getter method for transaction type of EnvelopeInfo instance
	 * @return transactionType EnvelopeType
	 */
	public EnvelopeType getTransactionTypeID() {
		return this.instance.getType();
	}

	/**
	 * Getter method for transaction MSPID private key of EnvelopeInfo instance
	 * @return privateKey String
	 */
	public String getTransactionMSPIDCreator() {
		return this.instance.getCreator().getMspid();
	}

	/**
	 * Getter method for transaction MSPID certificate of EnvelopeInfo instance
	 * @return certificate String
	 */
	public String getTransactionMSPIDCert() {
		return this.instance.getCreator().getId();
	}

	/**
	 * Getter method for transaction envelope info of EnvelopeInfo instance
	 * @return transactionEnvelopeInfo TransactionEnvelopeInfo
	 */
	public TransactionEnvelopeInfo getTransactionEnvelopeInfo() {
		return (TransactionEnvelopeInfo) this.instance;
	}

	/**
	 * Getter method for list of transaction Actions of EnvelopeInfo instance
	 * @return transactionEnvelopeInfoList List<TransactionEnvelopeInfo></TransactionEnvelopeInfo>
	 */
	public List<TransactionActionInfoClient> getTransactionActionInfos() {
		List<TransactionActionInfoClient> list = new ArrayList<TransactionActionInfoClient>();
		for(int x = 0 ; x< this.getTransactionActionInfoCount(); x++) {
			list.add(this.getTransactionActionInfo(x));
		}
		return list;
	}

	/**
	 * Getter method for transaction Action Info of EnvelopeInfo instance
	 * @param num int
	 * @return transactionActionInfo TransactionActionInfoClient
	 */
	public TransactionActionInfoClient getTransactionActionInfo( int num) {
		return new TransactionActionInfoClient(((TransactionEnvelopeInfo) this.instance).getTransactionActionInfo(num));
	}

	/**
	 * Getter method for number of tranasction info actions of EnvelopeInfo instance
	 * @return numberOfTransactionInfos int
	 */
	public int getTransactionActionInfoCount() {
		return this.getTransactionEnvelopeInfo().getTransactionActionInfoCount();
	}

	/**
	 * Getter method if the transaction was valid or not
	 * @return isTransactionValid boolean
	 */
	public boolean isTransactionValid() {
		return this.getTransactionEnvelopeInfo().isValid();
	}

	/**
	 * Getter method of transaction action validation code
	 * @return validationCode int
	 */
	public int getTransactionActionValidationCode() {
		return this.getTransactionEnvelopeInfo().getValidationCode();
	}
	
	
}