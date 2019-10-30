package com.ibm.federation.fabric;

import com.google.common.io.Files;
import com.ibm.federation.fabric.client.BlockClient;
import com.ibm.federation.fabric.client.EnvelopeInfoClient;
import com.ibm.federation.fabric.client.TransactionActionInfoClient;
import com.ibm.federation.fabric.network.FabricConnection;
import com.ibm.federation.fabric.user.FabricUser;
import com.ibm.federation.fabric.utils.Utilities;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

public class QueryBlockchainTable3 {
    private static FabricConnection connection;

    public static void main(String args []) throws Exception {

        if(args.length < 1){
            System.err.println("1 ARGUMENT EXPECTED");
            System.exit(206);
        }
        QueryBlockchainTable3 t = new QueryBlockchainTable3();
        t.init(args[0]);
        // t.init("/Users/ibm/Work/bigsql-fabric/config.json");
        t.queryBlockchain();
        connection.close();
    }

    private void queryBlockchain() throws InvalidArgumentException, ProposalException, UnsupportedEncodingException {
        System.out.println("\n------------------------------------------QUERIED BLOCKCHAIN INFORMATION---------------------------------------------------");
        BlockchainInfo info = connection.getBlockChainInfo();
        User user = connection.getHFChannelClient().getHFClient().getUserContext();
        FabricUser fabricUser = (FabricUser) user;
        System.out.println("QUERIED BLOCKCHAIN INFORMATION");
        System.out.println("Channel info for : " + connection.getHFChannelClient().getChannel().getName());
        System.out.println("Channel height: " + info.getHeight());
        System.out.println("User: " + fabricUser.getAffiliation() + " " + fabricUser.getName() + " " + fabricUser.getMspId());
        String chainCurrentHash = Utilities.hashToString(info.getCurrentBlockHash());
        String chainPreviousHash = Utilities.hashToString(info.getPreviousBlockHash());
        System.out.println("Chain current block hash: " + chainCurrentHash);
        System.out.println("Chain previous block hash: " + chainPreviousHash);
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("\nBLOCK NUMBER\t\t\tPREVIOUS BLOCK HASH\t\t\t\t\t\tCURRENT BLOCK HASH\t\t\t\t\t\tCURRENT DATA HASH\n");

        Collection<BlockClient> list = connection.queryBlockByRange(0,info.getHeight());

        System.out.printf("\n\nBLOCK NUMBER\t\t\t\tTRANSACTION ID\t\t\t\t\tTIMESTAMP\t\t\tFUNC NAME\tPARAM#\t\t\tPARAMATERS-->\n");

        long blockNum = 0;
        long maxBlockNum = info.getHeight();
        int envelopeCount= 0;
        int transactionCount =0;
        int paramIndex = 0;
        ArrayList<BlockClient> blockList = (ArrayList)list;
        while(true) {
            if (blockNum >= maxBlockNum) {
                break;
            }
            BlockClient returnedBlock = blockList.get((int)blockNum);
            List<EnvelopeInfoClient> enInfo = returnedBlock.getEnvelopeInfos();
            if (envelopeCount >= enInfo.size()) {
                envelopeCount = 0;
                transactionCount = 0;
                blockNum++;
                continue;
            } else {
                EnvelopeInfoClient envelope = enInfo.get(envelopeCount);
                if (envelope.getTransactionTypeID() == TRANSACTION_ENVELOPE) {
                    List<TransactionActionInfoClient> transactionInfo = envelope.getTransactionActionInfos();
                    if (transactionCount >= transactionInfo.size()) {
                        transactionCount = 0;
                        envelopeCount++;
                        continue;
                    } else {
                        long blockNumber = returnedBlock.getBlockNumber();
                        TransactionActionInfoClient transaction = transactionInfo.get(transactionCount);
                        String transactionID = envelope.getTransactionID();
                        String date = envelope.getTransactionTimeStamp().toString();
                        int paramCount = transaction.getChaincodeInputArgsCount();
                        if (paramIndex >= paramCount) {
                            paramIndex = 0;
                            transactionCount++;
                            continue;
                        } else {
                            if(paramIndex == 0)
                                paramIndex++;
                            System.out.printf("%5d%70s%33s", blockNumber,transactionID,date);
                            String func = Utilities.printableString(new String(transaction.getChaincodeInputArg(0), "UTF-8"));
                            String param = Utilities.printableString(new String(transaction.getChaincodeInputArg(paramIndex), "UTF-8"));
                            System.out.printf("%20s%10d%55s\n",func,paramIndex,param);
                            paramIndex++;
                        }
                    }
                } else {
                    envelopeCount++;
                }
            }
        }
    }

    public void init(String filePath) {
        if(!Files.getFileExtension(filePath).equals("json")){
            System.err.println("JSON FILE REQUIRED.");
            System.exit(206);
        }
        connection = Utilities.parseConfigFile(new File(filePath));
        if(connection == null){
            System.err.println("CONNECTION COULD NOT BE ESTABLISHED");
            System.exit(210);
        }
    }
}
