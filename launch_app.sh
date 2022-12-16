#!/usr/bin/env bash

master=$1
node=$2

#MASTER_NODE=$master NODE_ID=$node java -jar build/libs/assn2-0.0.1-SNAPSHOT.jar

ssh $node "MASTER_NODE=$master NODE_ID=$node java -jar /build/libs/assn2-0.0.1-SNAPSHOT.jar"
#
#while [ x != "x$2" ] ; do
#  if [ -h "$2" ]; then
#      break
#  else
#    echo "MASTER_NODE=$master NODE_ID=$2 java -jar build/libs/assn2-0.0.1-SNAPSHOT.jar"
#  fi
#  shift
#done


# MASTER_NODE=$master NODE_ID=$node java -jar build/libs/assn2-0.0.1-SNAPSHOT.jar


# sed -i -e 's/\r$//' gradlew