#!/usr/bin/env bash

master=${1}
node=${2}

MASTER_NODE=$master NODE_ID=$node java -jar build/libs/assn2-0.0.1-SNAPSHOT.jar

# sed -i -e 's/\r$//' gradlew