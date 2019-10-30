#!/bin/bash

#create jar file 
mvn clean
mvn package

#move jar file 
cp target/blockchain_connection_bigsql-1.0-SNAPSHOT-jar-with-dependencies.jar target/blockchain-fabric.jar
cp target/blockchain-fabric.jar ../
echo blockchain-fabric.jar file is located in bigsql directory
