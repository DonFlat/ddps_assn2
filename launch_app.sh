#!/usr/bin/env bash

MASTER_NODE=node102 IS_MASTER=false java -jar build/libs/assn2-0.0.1-SNAPSHOT.jar

# sed -i -e 's/\r$//' gradlew