#!/bin/bash

cd network

#teardown any existing docker images 
./teardown.sh

#build docker images
./build.sh
echo BLOCKCHAIN NETWORK RUNNING!
#create jar file blockchain application 
cd ../java
mvn install
cd target
cp blockchain-java-sdk-0.0.1-SNAPSHOT-jar-with-dependencies.jar blockchain-client.jar
cp blockchain-client.jar ../../network_resources
cd ../../network_resources

#create channel
echo RUNNING JAVA CLASSES TO SET UP CHANNEL, CHAINCODE AND USERS
java -cp blockchain-client.jar org.app.network.CreateChannel

#deploy chaincode fabcar
java -cp blockchain-client.jar org.app.network.DeployInstantiateChaincode

#register all users to channel
java -cp blockchain-client.jar org.app.user.RegisterEnrollUser
echo BLOCKCHAIN NETWORK SET UP COMPLETE!
