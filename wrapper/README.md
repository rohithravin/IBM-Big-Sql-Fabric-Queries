# MORE ABOUT THE PROJECT CODE

## Config.json
In order to connect to an existing hyperleger fabric blockchain, it requires a lot of credentails and keys. When it comes to the user, we need to know the username, organization affiliations, organization mspid, and your private key and certificate. In some cases if you do not give the direct file path of the key and cert, the code will also be able to generate it. When it comes to the the blockchain itself, you will need the IP address of where the blockchain is running and the peer name, url, etc. 

```
{
    "fabricUser":{
        "username":"admin",
        "password":"adminpw",
         "userAffiliation":"org1",
         "userMSPID":"Org1MSP",
         "userCertFilePath":"network_resources/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/admincerts",
         "userPKFilePath":"network_resources/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore"
    },
     "network":{
         "channelName":"mychannel",
         "IPAdress":"127.0.0.1",
         "peerName":"peer0.org1.example.com",
         "peerURLPort":"7051",
         "ordererName":"orderer.example.com",
         "ordererURLPort":"7050",
         "caURLPort":"7054"
    }
}
```

When we run a specific java class such as BlockchainTable1.java, the project will parse through the json file getting all the information and passing it as parameters for the main object class that controls the connection.

If you want to connect to a different blockchain, make sure you edit this file so all the attibutes are correct, otherwise you won't be albe to connect.

Links:

1. [Config.json](https://github.ibm.com/rohithravin/bigsql-fabric/blob/master/config.json) 

## FabricConnection.java
This is the main object class that will connect to the blockchain and run all the query functions from both the blockchain and ledger. 

This is one of the many constructors in this object. From the constructor we can see the basic structure of how the connection is established. We the paramters for the constructor are all the attributes in the config.json file. You can also see that from the information provided we create a user and register/enroll it with the certficate authority associated with that organziation. This generates the private key and certifcate allowing the user to send transactaions and query information. 

```
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
```
These methods below are the avaiable query functions to query the blockchain. The main methods are the last 4. Those are related to the QSCC Request. From the Java SDK we will be using there are mnay ways to query information of a blockchain database, however the most efficient way avaiable is by QSCC. Through QSCC we are able to query a block from the blockchain via block number, block hash value, transaction id. We also implmemeted a function to query a range of functions to improve speed. 

```
	public BlockchainInfo getBlockChainInfo() 

	public Collection<BlockClient> queryBlockByRange( long start_num , long end_num) 

	public BlockClient queryByBlockNumber( long number) 

	public BlockClient queryByBlockHash( byte [] hash)

	public BlockClient queryBlockByTransactionID( String txnId)

	public TransactionInfo queryByTransactionId( String txnId)
```

This is the only method avaiable for query from the ledger. This is because in order to query from the ledger, we need to interact with the chaincode itself. If the query function is not avaiable within the chaincode, then the user will not be able to query information from the ledger. For testing purposes we have implemented multiple query functions in our chaincode ```fabcar.go```. 

```
	public Collection<ProposalResponse> queryBlockChain( String chaincode, String chaincodeFunction, String [] arguments) throws Exception{
		Collection<ProposalResponse>  responsesQuery = this.getHFChannelClient().queryByChainCode(chaincode, chaincodeFunction, arguments);
		return responsesQuery;
	}
```
Links:

1. [FabricConnection.java](https://github.ibm.com/rohithravin/bigsql-fabric/blob/master/wrapper/src/main/java/com/ibm/federation/fabric/network/FabricConnection.java)  


## User Folder 
Both FabricUser and FabricEnrollment are used to store information about the user who is connected to the network. FabricUser stores the the username, mspid, affiliation etc. The FabricEnrollment stores the necessary signatures of the user when trying to query or send a transaction to the network - private key and certificate. This information can we generated by our code or if you already have the key and cert you may insert the file paths into the config.json.

Links:

1. [FabricUser.java](https://github.ibm.com/rohithravin/bigsql-fabric/blob/master/wrapper/src/main/java/com/ibm/federation/fabric/user/FabricUser.java) 
2. [FabricEnrollment.java](https://github.ibm.com/rohithravin/bigsql-fabric/blob/master/wrapper/src/main/java/com/ibm/federation/fabric/user/FabricEnrollment.java) 

## Client Folder
When it comes to interacting with the blockchain network, the java sdk has many class objects and work together to accomplish this. However for us, we don't want to use and have that many classes. For that case, we have created client classes where each encapsulate at least more than one class object from the sdk. This way, we minimize code and still have some control over functionality. 

The most important client class is HFChannelClient. This class controls everything that has to do with a specific channel and the chaincode associated with the channel. This includes sending transactions and querying information form the ledger, as both need to interact directly with the chaincode. 

This method is called by the ```queryByChainCode()``` from ```FabricConnection.java```. If there are multiple chaincodes associated with the channel, you have the flexibility of choosing with chaincode you want to query from. The method will return the query ouput in the from of the .json. However, this is specific to your chaincode we are using, since when we insert the assets into the ledger we using json encoding. 

```
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
```
Links:

1. [HFChannelClient.java](https://github.ibm.com/rohithravin/bigsql-fabric/blob/master/wrapper/src/main/java/com/ibm/federation/fabric/client/HFChannelClient.java)

## User Defined Functions

As part of our project, we want to implement a couple of UDF's within BigSql Db2 as a way for users and data scientists to write one SQL statement and query all the information requested for the blockchain network

Understand what and how UDF's are used by following the link [here](https://www.ibm.com/support/knowledgecenter/en/SSEPEK_10.0.0/sqlref/src/tpc/db2z_userdefinedfunctionssql.html).

In order to create the UDF in Db2, we need to first create the functions in java, which is done in ```UDF.java```.

Below is one of the UDF functions defined. Each method in the class corresponds to a specific UDF funtion which is associated by the method name. Right now the methods we have written are mainly for testing purpose and haven't been made to be flexibile. 

```
public void getChannelBlocks(String channelName, String configFilePath) throws Exception {
		int inputsCount = 1; // The number of params to the function (see below)
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
```

For for information about UDF's with respect to this project, please take a look at the boxnote [here](https://ibm.ent.box.com/file/299915288569).

Links:

1. [UDF.java](https://github.ibm.com/rohithravin/bigsql-fabric/blob/master/wrapper/src/main/java/com/ibm/federation/fabric/udf/FabricUDF.java)

## Optional: More Information About The Chaincode

The chaincode (also called the smart contract), determines what is happening within the chaincode. Essentially, it is where all the transaction proposals go through before affect the ledger. 

From the imports, we noticed that all of your data will have json encoding. Also, the last two imports are related to the Hyperledger Fabric GO-LANG API's. ```"github.com/hyperledger/fabric/core/chaincode/shim"``` is how the chaincode is able to manipulate the assets in the ledger database. While ```"github.com/hyperledger/fabric/protos/peer"```,  is the API that allows the chaincode to interact with the java sdk.

```
import (
	"bytes"
	"encoding/json"
	"fmt"
	"strconv"
	"strings"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)
```

You can view all avaiable API's of the package shim [here](https://godoc.org/github.com/hyperledger/fabric/core/chaincode/shim).

Below we see the type of data structure we have for all the assests stored in the ledger. We noticed that data structure represents that of a bank account. Notice also how all the types have json encoding. This will help us when trying to read and write information into the ledger.

```
type marble struct {
	ObjectType string  `json:"docType"`
	Name string        `json:"accountid"` 
	Color               string `json:"owner"`
	Size                int    `json:"amount"`
}
```

Below is the list of functions that are available within the chaincode. As you can see, we have both Query Functions, and Transaction Functions in the chaincode. The query functions retrieve information directly from the ledger database and return the output as bytes. The transactions functions will directly manipulate the assets within the ledger database. All of these interactions with the database are done directly with the Go-Lang shim API's. 

```
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "initAccount" { //create a new marble
		return t.initAccount(stub, args)
	} else if function == "readAccount" { //read a marble
		return t.readAccount(stub, args)
	} else if function == "getHistoryForAccount" { //read a marble
		return t.getHistoryForAccount(stub, args)
	} else if function == "closeAccount" { //read a marble
		return t.closeAccount(stub, args)
	} else if function == "transferAccount" { //read a marble
		return t.transferAccount(stub, args)
	} else if function == "depositMoney" { //read a marble
		return t.depositMoney(stub, args)
	} else if function == "withdrawMoney" { //read a marble
		return t.withdrawMoney(stub, args)
	} else if function == "transferMoney" { //read a marble
		return t.transferMoney(stub, args)
	} else if function == "getAccountsByRange" { //read a marble
		return t.getAccountsByRange(stub, args)
	}
	return shim.Error("Received unknown function invocation")
}
}
```
Links:

1. [fabcar.go](https://github.ibm.com/rohithravin/bigsql-fabric/blob/master/blockchain-marblebank-application/network_resources/chaincode/src/github.com/fabcar/fabcar.go)

