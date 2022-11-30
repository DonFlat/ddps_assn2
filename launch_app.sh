#!/usr/bin/env bash

MASTER_NODE=node102 IS_MASTER=false NODE_ID=node103 java -jar build/libs/assn2-0.0.1-SNAPSHOT.jar
MASTER_NODE=node102 IS_MASTER=true NODE_ID=node102 java -jar build/libs/assn2-0.0.1-SNAPSHOT.jar

# sed -i -e 's/\r$//' gradlew