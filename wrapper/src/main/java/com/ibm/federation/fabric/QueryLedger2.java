package com.ibm.federation.fabric;

import com.google.common.io.Files;
import com.ibm.federation.fabric.client.BlockClient;
import com.ibm.federation.fabric.client.EnvelopeInfoClient;
import com.ibm.federation.fabric.client.TransactionActionInfoClient;
import com.ibm.federation.fabric.network.FabricConnection;
import com.ibm.federation.fabric.utils.Utilities;
import org.hyperledger.fabric.sdk.ProposalResponse;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

public class QueryLedger2 {
    private static FabricConnection connection;

    public static void main(String args []) throws Exception {
        if(args.length < 1){
            System.err.println("1 ARGUMENT EXPECTED");
            System.exit(206);
        }
        QueryLedger2 t = new QueryLedger2();
        t.init(args[0]);
        // t.init("/Users/ibm/Work/bigsql-fabric/config.json");
        t.queryLedger(connection);
        connection.close();
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

    private void queryLedger(FabricConnection connection) throws Exception {
        Thread.sleep(3000);
        System.out.println("\n---------------------------------QUERY TRANSACTION HISTORY OF ACCOUNT----------------------------------");
        System.out.println("\nAccount History for account17\n");
        String[] args2 = {"account17"};
        Collection<ProposalResponse> responses1Query = connection.queryBlockChain( "fabcar","getHistoryForAccount", args2);
        printAccountHistory(responses1Query);
        System.out.println("\nAccount History for account 01\n");
        String[] args3 = {"account01"};
        responses1Query =  connection.queryBlockChain("fabcar","getHistoryForAccount", args3);
        printAccountHistory(responses1Query);
        System.out.println();
    }

    private void printAccountHistory(Collection<ProposalResponse> responses) throws Exception {
        for (ProposalResponse pres : responses) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            String [] txids = stringResponse.split(",");
            System.out.printf("\n\n\tBLOCK NUMBER\t\t\t\tTRANSACTION ID\t\t\t\t\t\tTIMESTAMP\t\t   FUNC NAME\t   PARAMATERS-->\n");
            for(String tx : txids){
                BlockClient block = connection.queryBlockByTransactionID( tx);
                List<EnvelopeInfoClient> enInfo = block.getEnvelopeInfos();
                for(int x = 0; x < enInfo.size(); x++){
                    EnvelopeInfoClient envelope = enInfo.get(x);
                    if (envelope.getTransactionTypeID() == TRANSACTION_ENVELOPE) {
                        List<TransactionActionInfoClient> transactionInfo = envelope.getTransactionActionInfos();
                        for(int y = 0; y < transactionInfo.size(); y++){
                            TransactionActionInfoClient transaction = transactionInfo.get(y);
                            if ( envelope.getTransactionID().equals(tx)) {
                                System.out.printf("\t%6d%73s%35s", block.getBlockNumber(), envelope.getTransactionID(), envelope.getTransactionTimeStamp().toString());
                                String func = Utilities.printableString(new String(transaction.getChaincodeInputArg(0), "UTF-8"));
                                System.out.printf("%20s", func);
                                for(int z = 1; z < transaction.getChaincodeInputArgsCount(); z ++){
                                    System.out.printf("%15s", Utilities.printableString(new String(transaction.getChaincodeInputArg(z), "UTF-8")));
                                }
                                System.out.println("");
                            }
                        }
                    }
                }
            }
        }
        System.out.println();
    }
}
