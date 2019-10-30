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

import org.hyperledger.fabric.sdk.BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.protos.common.Common.Payload;

/**
 * @author Rohith Ravindranath
 * @version June 29 2018
 * Wrapper class for TransactionActionInfo
 */
public class TransactionActionInfoClient{
	
	final TransactionActionInfo instance;

	/**
	 * Constructor
	 * @param instance
	 */
	public TransactionActionInfoClient(TransactionActionInfo instance) {
		this.instance = instance;
	}

	/**
	 * Getter method for response status of transaction
	 * @return responseStatus int
	 */
	public int getResponseStatus() {
		return this.instance.getResponseStatus();
	}

	/**
	 * Getter method for response message of transaction
	 * @return responseMessage byte[]
	 */
	public byte[] getResponseMessage() {
		return this.instance.getResponseMessageBytes();
	}

	/**
	 * Getter method for input argument count of transaction
	 * @return argCount int
	 */
	public int getChaincodeInputArgsCount() {
		return this.instance.getChaincodeInputArgsCount();
	}

	/**
	 * Getter method for proposal response status of transaction
	 * @return reponseStatus int
	 */
	public int getProposalResponseStatus() {
		return this.instance.getProposalResponseStatus();
	}

	/**
	 * Getter method for event payload  of transaction
	 * @return eventPayload byte[]
	 */
	public byte[] getEventPayload() {
		if(this.instance.getEvent() != null) {
			if(this.instance.getEvent().getPayload() != null)
				return this.instance.getEvent().getPayload();
		}
		return null;
	}

	/**
	 * Getter method for event transaction id of transaction
	 * @return transactionID String
	 */
	public String getEventTransactionID() {
		if(this.instance.getEvent() != null) {
			if(this.instance.getEvent().getTxId() != null)
				return this.instance.getEvent().getTxId();
		}
		return null;
	}

	/**
	 * Getter method for event chaincode id of transaction
	 * @return chaincode String
	 */
	public String getEventChaincodeID() {

		if(this.instance.getEvent() != null) {
			if(this.instance.getEvent().getChaincodeId() != null)
				return this.instance.getEvent().getChaincodeId();
		}
		return null;
	}

	/**
	 * Getter method for event name of transaction
	 * @return eventName String
	 */
	public String getEventName() {
		if(this.instance.getEvent() != null) {
			if(this.instance.getEvent().getEventName() != null)
				return this.instance.getEvent().getEventName();
		}
		return null;
	}

	/**
	 * Getter method for proposal response payload of transaction
	 * @return proposalPayload byte[]
	 */
	public byte[] getProposalResponsePayload() {
		return this.instance.getProposalResponsePayload();
	}

	/**
	 * Getter method for proposal response message of transaction
	 * @return proposalMssage byte[]
	 */
	public byte[] getProposalResponseMessage() {
		return this.instance.getProposalResponseMessageBytes();
	}

	/**
	 * Getter method for chaincode input argument of transaction
	 * @param num int
	 * @return inputArg byte[]
	 */
	public byte[] getChaincodeInputArg(int num) {
		return this.instance.getChaincodeInputArgs(num);
	}

	/**
	 * Getter method for number of endorsers of transaction
	 * @return numOfEndorsers int
	 */
	public int getEndorsementCount() {
		return this.instance.getEndorsementsCount();
	}

	/**
	 * Getter method for specific endorser signature of transaction
	 * @param num  int
	 * @return endorserSig byte[]
	 */
	public byte[] getEndorserSig(int num) {
		return this.instance.getEndorsementInfo(num).getSignature();
	}

	/**
	 * Getter method for specific endorser certificate of transaction
	 * @param num  int
	 * @return endorserCert String
	 */
	public String getEndorserCert(int num) {
		return this.instance.getEndorsementInfo(num).getId();
	}

	/**
	 * Getter method for specific endorser MSPID of transaction
	 * @param num  int
	 * @return endorserMSPID String
	 */
	public String getEndorserMSPID(int num) {
		return this.instance.getEndorsementInfo(num).getMspid();
	}

}