package com.ibm.federation.fabric;

import com.google.common.io.Files;
import com.ibm.federation.fabric.network.FabricConnection;
import com.ibm.federation.fabric.utils.Utilities;
import org.hyperledger.fabric.sdk.ProposalResponse;

import java.io.File;
import java.util.Collection;

public class QueryLedger3 {
    private static FabricConnection connection;

    public static void main(String args []) throws Exception {
        if(args.length < 1){
            System.err.println("1 ARGUMENT EXPECTED");
            System.exit(206);
        }
        QueryLedger3 t = new QueryLedger3();
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
        System.out.println("\n---------------------------------QUERY ENTIRE WORLD OF STATE----------------------------------");
        String[] args13 = {"account01","account99"};
        Collection<ProposalResponse> responses1Query = connection.queryBlockChain("fabcar", "getAccountsByRange", args13);
        for (ProposalResponse pres : responses1Query) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            String [] accounds = stringResponse.split("}},");
            for(String accound : accounds){
                System.out.println(accound + "}");
            }
        }
        System.out.println();
    }

}
