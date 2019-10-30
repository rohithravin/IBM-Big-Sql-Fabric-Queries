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
import java.util.Collection;

import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

public class QueryBlockchainTable2 {
    private static FabricConnection connection;

    public static void main(String args []) throws Exception {

        if(args.length < 1){
            System.err.println("1 ARGUMENT EXPECTED");
            System.exit(206);
        }
        QueryBlockchainTable2 t = new QueryBlockchainTable2();
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
        for(BlockClient returnedBlock: list) {
            long blockNumber = returnedBlock.getBlockNumber();
            for (EnvelopeInfoClient envelopeInfo : returnedBlock.getEnvelopeInfos()) {
                if (envelopeInfo.getTransactionTypeID() == TRANSACTION_ENVELOPE) {
                    for (TransactionActionInfoClient transactionActionInfo : envelopeInfo.getTransactionActionInfos()) {
                        final String channelId = envelopeInfo.getChannelID();
                        System.out.printf("%5d%75s%35s", blockNumber, envelopeInfo.getTransactionID(), envelopeInfo.getTransactionTimeStamp());
                        System.out.printf("%20s\n", Utilities.printableString(new String(transactionActionInfo.getChaincodeInputArg(0), "UTF-8")));
                    }
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
