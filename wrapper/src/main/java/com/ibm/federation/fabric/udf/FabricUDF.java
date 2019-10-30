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
package com.ibm.federation.fabric.udf;

import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

import java.io.File;
import java.util.List;

import com.google.common.io.Files;
import org.hyperledger.fabric.sdk.BlockchainInfo;

import com.ibm.federation.fabric.client.BlockClient;
import com.ibm.federation.fabric.client.EnvelopeInfoClient;
import com.ibm.federation.fabric.client.TransactionActionInfoClient;
import com.ibm.federation.fabric.network.FabricConnection;
import com.ibm.federation.fabric.utils.Utilities;

import COM.ibm.db2.app.UDF;

/**
 * @author Rohith Ravindranath
 * @version June 29 2018
 * User Define Function class that will be used with Db2 to connect and query blocks from a blockchain network
 */
public class FabricUDF extends UDF {

	private long blockNum;
	private long maxBlockNum;
	private FabricConnection connection;
	private int envelopeCount;
	private int transactionCount;
	private int paramIndex;

	private int blockId;
	private int maxBlockId;

	/**
	 * Constructor
	 */
	public FabricUDF() {

	}

	/**
	 * Function that will be registered in the Db2 database as a udf function.
	 * Will return a table of information of all the blocks in the connected blockchain
	 * @param channelName
	 * @param configFilePath
	 */
	public void getChannelBlocks(String channelName, String configFilePath) throws Exception {

		int inputsCount = 1; // The number of params to the function (see below)

		// This code is a placeholder for now, showing how to return data
		// It returns a "fake" table with those columns:
		// INT ID, VARCHAR NAME, FLOAT VALUE

		switch (getCallType()) {
		    case SQLUDF_TF_OPEN:		// connect to the remote source
		    		blockNum = 1;
				if(!Files.getFileExtension( configFilePath).equals("json")){
					System.err.println("JSON FILE REQUIRED.");
					System.exit(206);
				}
				connection = Utilities.parseConfigFile(new File(configFilePath));
				if(connection == null){
					System.err.println("CONNECTION COULD NOT BE ESTABLISHED");
					System.exit(210);
				}
		    		BlockchainInfo info = connection.getBlockChainInfo();
		    		maxBlockNum = info.getHeight();
		    		break;

		    case SQLUDF_TF_FETCH: 	// read & set the next row - in our case, it could be the next block
		    		if (blockNum >= maxBlockNum || blockNum < 0) {
		    			setSQLstate("02000");
		    			return;
		    		}
		    		this.set(inputsCount+1, blockNum);
		    		BlockClient returnedBlock = connection.queryByBlockNumber( blockNum);
		           String channelID = returnedBlock.getChannelId();
		    		String previousHash = Utilities.hashToString(returnedBlock.getPreviousHash());
		    		String currentHash = Utilities.hashToString(returnedBlock.getHash(connection));
		    		String dataHash = Utilities.hashToString(returnedBlock.getDataHash());
		    		this.set(inputsCount + 1, blockNum);	// LONG
		    		this.set(inputsCount + 2, channelID);	// VARCHAR
		    		this.set(inputsCount + 3, previousHash);// VARCHAT
		    		this.set(inputsCount + 4, currentHash);	// VARVHAR
		    		this.set(inputsCount + 5, dataHash);	// VARCHAR
		    		blockNum++;
		    		break;
		    		
		    case SQLUDF_TF_CLOSE:	// close the remote connection
		    		connection.close();
		    		connection = null;
		    		blockNum = 1;
		    		maxBlockNum = 1;
		    		transactionCount = 0;
		    		envelopeCount = 0;
		    		break;	
		}
	}

	/**
	 * Function that will be registered in the Db2 database as a udf function.
	 * Will return a table of information of all the transactions in the connected blockchain
	 * @param channelName
	 * @param configFilePath
	 */
	public void getTransactions(String channelName, String configFilePath) throws Exception {
		int inputsCount = 1; // The number of params to the function (see below)

		// This code is a placeholder for now, showing how to return data
		// It returns a "fake" table with those columns:
		// INT ID, VARCHAR NAME, FLOAT VALUE

		switch (getCallType()) {
		    case SQLUDF_TF_OPEN:		// connect to the remote source
		    		blockNum = 1;
				if(!Files.getFileExtension(configFilePath).equals("json")){
					System.err.println("JSON FILE REQUIRED.");
					System.exit(206);
				}
				connection = Utilities.parseConfigFile(new File(configFilePath));
				if(connection == null){
					System.err.println("CONNECTION COULD NOT BE ESTABLISHED");
					System.exit(210);
				}
		    		BlockchainInfo info = connection.getBlockChainInfo();
		    		maxBlockNum = info.getHeight();
		    		envelopeCount = 0;
		    		transactionCount = 0;
		    		break;

		    case SQLUDF_TF_FETCH: 	// read & set the next row - in our case, it could be the next block
			    	if (blockNum >= maxBlockNum || blockNum < 0) {
			    		setSQLstate("02000");
			 			return;
			 		}
			 		BlockClient returnedBlock = connection.queryByBlockNumber( blockNum);
			 		List<EnvelopeInfoClient> enInfo = returnedBlock.getEnvelopeInfos();
			 		if (envelopeCount >= enInfo.size()) {
			 			envelopeCount = 0;
			 			transactionCount = 0; 
			 			blockNum++;
			 			break;
			 		}
			 		else{
			 			//System.out.println("h2\n");
			 			EnvelopeInfoClient envelope = enInfo.get(envelopeCount);
			 			if(envelope.getTransactionTypeID() == TRANSACTION_ENVELOPE) {
				    			List<TransactionActionInfoClient> transactionInfo =  envelope.getTransactionActionInfos();
				    			if(transactionCount >= transactionInfo.size()) {
				    				transactionCount = 0;
				    				envelopeCount++;
				    				break;
				    			}
				    			else{
				    				TransactionActionInfoClient transaction = transactionInfo.get(transactionCount);
				    				long blockNumber = returnedBlock.getBlockNumber(); 
				    				String transactionID = envelope.getTransactionID();
				    				String channelID = returnedBlock.getChannelId();
				    				String date = envelope.getTransactionTimeStamp().toString();
				    				String submitterMSPID = envelope.getTransactionMSPIDCreator();
				    				int responseStatus = transaction.getResponseStatus();
				    				int endorsementCount = transaction.getEndorsementCount();
				    				this.set(inputsCount + 1, blockNumber);	// LONG
						    		this.set(inputsCount + 2, channelID);	// VARCHAR 
						    		this.set(inputsCount + 3, transactionID);// VARCHAT 
						    		this.set(inputsCount + 4, date);	// VARVHAR NAME
						    		this.set(inputsCount + 5, submitterMSPID);	// VARCHAR 
						    		this.set(inputsCount + 6, responseStatus);	// INT
						    		this.set(inputsCount + 7, endorsementCount);	// INT
				    			}
				    			transactionCount++;
			 			}
			 			else {
			 				envelopeCount++;
			 			}
			 		}
			 		break;	
		    case SQLUDF_TF_CLOSE:	// close the remote connection
		    		connection.close();
		    		transactionCount = 0;
		    		envelopeCount = 0;
		    		blockNum = 1;
		    		maxBlockNum = 1;
		    		connection = null;
		    		break;		
		}
	}

	/**
	 * Function that will be registered in the Db2 database as a udf function.
	 * Used for testing purposes
	 * @param channelName
	 */
	public void testUDF(String channelName) throws Exception {
		int inputsCount = 1; // The number of params to the function (see below)

		// This code is a placeholder for now, showing how to return data
		// It returns a "fake" table with those columns:
		// INT ID, VARCHAR NAME, FLOAT VALUE

		switch (getCallType()) {
			case SQLUDF_TF_OPEN:		// connect to the remote source
				blockId = 1;
				maxBlockId = (int) Math.round(Math.random() * 50);

				break;

			case SQLUDF_TF_FETCH: 	// read & set the next row - in our case, it could be the next block

				if (blockId >= maxBlockId) {

					// All the rows have been returned

					setSQLstate("02000");
					return;
				}

				this.set(inputsCount + 1, blockId);						// INT ID
				this.set(inputsCount + 2, channelName + " Block #" + blockId);	// VARCHAR NAME
				this.set(inputsCount + 3, Math.random() + blockId);		// FLOAT VALUE

				blockId ++;

				break;

			case SQLUDF_TF_CLOSE:	// close the remote connection
				break;
		}
	}

	/**
	 * Function that will be registered in the Db2 database as a udf function.
	 * Will return a table of information of all the parameters of the transactions in the connected blockchain
	 * @param channelName
	 * @param configFilePath
	 */
	public void getTransactionParams(String channelName, String configFilePath) throws Exception {
		int inputsCount = 1; // The number of params to the function (see below)

		// This code is a placeholder for now, showing how to return data
		// It returns a "fake" table with those columns:
		// INT ID, VARCHAR NAME, FLOAT VALUE

		switch (getCallType()) {
		    case SQLUDF_TF_OPEN:		// connect to the remote source
		    		blockNum = 1;
				if(!Files.getFileExtension(configFilePath).equals("json")){
					System.err.println("JSON FILE REQUIRED.");
					System.exit(206);
				}
				connection = Utilities.parseConfigFile(new File(configFilePath));
				if(connection == null){
					System.err.println("CONNECTION COULD NOT BE ESTABLISHED");
					System.exit(210);
				}
		    		BlockchainInfo info = connection.getBlockChainInfo();
		    		maxBlockNum = info.getHeight();
		    		envelopeCount = 0;
		    		transactionCount = 0;
		    		paramIndex = 0;
		    		break;

		    case SQLUDF_TF_FETCH: 	// read & set the next row - in our case, it could be the next block
		 	        if (blockNum >= maxBlockNum || blockNum < 0) {
		 	        	setSQLstate("02000");
		 				return;
		 			}
		 			BlockClient returnedBlock = connection.queryByBlockNumber( blockNum);
		 			List<EnvelopeInfoClient> enInfo = returnedBlock.getEnvelopeInfos();
		 			if (envelopeCount >= enInfo.size()) {
		 				envelopeCount = 0;
		 				transactionCount = 0; 
		 				blockNum++;
		 				break;
		 			}		
		 			else{
		 				EnvelopeInfoClient envelope = enInfo.get(envelopeCount);
		 				if(envelope.getTransactionTypeID() == TRANSACTION_ENVELOPE) {
		 	    			List<TransactionActionInfoClient> transactionInfo =  envelope.getTransactionActionInfos();
		 	    			if(transactionCount >= transactionInfo.size()) {
		 	    				transactionCount = 0;
		 	    				envelopeCount++;
		 	    				break;
		 	    			}
		 	    			else{
		 	    				long blockNumber = returnedBlock.getBlockNumber(); 
		 	    				TransactionActionInfoClient transaction = transactionInfo.get(transactionCount); 
		 	    				String transactionID = envelope.getTransactionID();
		 	    				String channelID = returnedBlock.getChannelId();
		 	    				String date = envelope.getTransactionTimeStamp().toString();
		 	    				int paramCount = transaction.getChaincodeInputArgsCount();   		
		                     	if(paramIndex >= paramCount) {
		                     		paramIndex = 0;
		                     		transactionCount++;
		                     		break;
		                     	}
		                     	else {
		                     		this.set(inputsCount + 1, blockNumber);
		                     		this.set(inputsCount + 2, transactionID);
		                     		this.set(inputsCount + 3, channelID);
		                     		this.set(inputsCount + 4, date);
		                     		this.set(inputsCount + 5, paramIndex+1);
		                     		String param  = Utilities.printableString(new String(transaction.getChaincodeInputArg(paramIndex), "UTF-8"));
		                     		this.set(inputsCount + 5, param);
		 	                    	paramIndex++;
		                     	}
		 			    		System.out.println();
		 	    			}  			
		 				}
		 				else {
		 					envelopeCount++;
		 				}
		 			}
		 			break;
		    case SQLUDF_TF_CLOSE:	// close the remote connection
		    		connection.close();
		    		transactionCount = 0;
		    		envelopeCount = 0;
		    		blockNum = 1;
		    		maxBlockNum = 1;
		    		connection = null;
		    		paramIndex = 0;
		    		break;		
		}
	}
	

}