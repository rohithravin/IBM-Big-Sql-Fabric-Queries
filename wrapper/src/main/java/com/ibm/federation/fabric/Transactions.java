package com.ibm.federation.fabric;

import com.google.common.io.Files;
import com.ibm.federation.fabric.network.FabricConnection;
import com.ibm.federation.fabric.utils.Utilities;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.File;
import java.util.Collection;

public class Transactions {

    private static FabricConnection connection;

    public static void main(String args []) throws Exception {
        if(args.length < 1){
            System.err.println("1 ARGUMENT EXPECTED");
            System.exit(206);
        }
        Transactions t = new Transactions();
        t.init(args[0]);
       // t.init("/Users/ibm/Work/bigsql-fabric/config.json");
        t.initLedger();
        t.addedTransactions();
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


    public void addedTransactions() throws Exception {
        System.out.println("Sending Transactions...");
        String[] arguments1 = {"account01","50"};
        sendTransaction("depositMoney",arguments1);

        String[] arguments2 = {"account02","50"};
        sendTransaction("depositMoney",arguments2);


        String[] arguments3 = {"account03","50"};
        sendTransaction("depositMoney",arguments3);

        String[] arguments4 = {"account04","50"};
        sendTransaction("depositMoney",arguments4);

        String[] arguments5 = {"account17","50"};
        sendTransaction("depositMoney",arguments5);

        String[] arguments6 = {"account17","20"};
        sendTransaction("depositMoney",arguments6);

        String[] arguments7 = {"account17","530"};
        sendTransaction("depositMoney",arguments7);

        String[] arguments8 = {"account17","230"};
        sendTransaction("depositMoney",arguments8);

        String[] arguments9 = {"account17","100"};
        sendTransaction("depositMoney",arguments9);

        String[] arguments10 = {"account17","account16","100"};
        sendTransaction("transferMoney",arguments10);

        String[] arguments11 = {"account17","80"};
        sendTransaction("withdrawMoney",arguments11);

        String[] arguments12 = {"account17","190"};
        sendTransaction("depositMoney",arguments12);

        String[] arguments13 = {"account17","50"};
        sendTransaction("withdrawMoney",arguments13);

        String[] arguments14 = {"account17","mars"};
        sendTransaction("transferAccount",arguments14);

        String[] arguments15 = {"account11"};
        sendTransaction("closeAccount",arguments15);

        String[] arguments16 = {"account01","1900"};
        sendTransaction("depositMoney",arguments16);

        String[] arguments17 = {"account01","account02","100"};
        sendTransaction("transferMoney",arguments17);

        String[] arguments18 = {"account01","account03","100"};
        sendTransaction("transferMoney",arguments18);

        String[] arguments19 = {"account01","account04","100"};
        sendTransaction("transferMoney",arguments19);

        String[] arguments20 = {"account01","account05","100"};
        sendTransaction("transferMoney",arguments20);

        String[] arguments21 = {"account01","account06","100"};
        sendTransaction("transferMoney",arguments21);

        String[] arguments22 = {"account01","mars"};
        sendTransaction("transferAccount",arguments22);

        String[] arguments23 = {"account01","200"};
        sendTransaction("withdrawMoney",arguments23);

        String[] arguments24 = {"account01","200"};
        sendTransaction("withdrawMoney",arguments24);

        System.out.println("Transactions Sent.");
    }

    public void sendTransaction(String funcName, String [] args) throws InvalidArgumentException, ProposalException, InterruptedException {
        Thread.sleep(4000);
        Collection<ProposalResponse> responses = connection.getHFChannelClient().sendTransactionProposal( "fabcar", funcName, args);
        for (ProposalResponse pres : responses) {
            String stringResponse = new String(pres.getStatus().toString());
            if(stringResponse.equalsIgnoreCase("failure")) {
                System.out.print("Transaction " + stringResponse + ": " + funcName);
                for (String str : args) {
                    System.out.print(" " + str);
                }
                System.out.println();
            }
        }
    }


    public void initLedger() throws ProposalException, InvalidArgumentException, InterruptedException {
        System.out.println("Creating world of state...");
        String[] arguments9 = { "account01","rohith", "50"};
        sendTransaction( "initAccount", arguments9);
        String[] arguments0 = { "account02","andrew","60"};
        sendTransaction( "initAccount", arguments0);
        String[] arguments1 = { "account03","mike","70"};
        sendTransaction("initAccount", arguments1);
        String[] arguments6 = { "account04","rohan","100"};
        sendTransaction("initAccount", arguments6);
        String[] arguments7 = { "account05","rishab","110"};
        sendTransaction("initAccount", arguments7);
        String[] arguments8 = { "account06","arjun","200"};
        sendTransaction( "initAccount", arguments8);
        String[] arguments10 = { "account07","leslie","201"};
        sendTransaction( "initAccount", arguments10);
        String[] arguments5 = { "account08","mary","220"};
        sendTransaction( "initAccount", arguments5);
        String[] arguments11 = { "account09","mack","203"};
        sendTransaction( "initAccount", arguments11);
        String[] arguments12 = { "account10","wayne","204"};
        sendTransaction( "initAccount", arguments12);
        String[] arguments20 = { "account11","maya","250"};
        sendTransaction( "initAccount", arguments20);
        String[] arguments13 = { "account12","shreya","100"};
        sendTransaction( "initAccount", arguments13);
        String[] arguments14 = { "account13","philip","120"};
        sendTransaction( "initAccount", arguments14);
        String[] arguments15 = { "account14","anushka","140"};
        sendTransaction( "initAccount", arguments15);
        String[] arguments16 = { "account15","riyana","350"};
        sendTransaction("initAccount", arguments16);
        String[] arguments17 ={ "account16","cardi","320"};
        sendTransaction( "initAccount", arguments17);
        String[] arguments18 = { "account17","bruno","250"};
        sendTransaction( "initAccount", arguments18);
        String[] arguments19 = { "account18","siya","520"};
        sendTransaction("initAccount", arguments19);
        String[] arguments22 = { "account19","robin","520"};
        sendTransaction("initAccount", arguments22);
        String[] arguments21 = { "account20","sameer","502"};
        sendTransaction( "initAccount", arguments21);
        System.out.println("Created world of state.");
    }
}
