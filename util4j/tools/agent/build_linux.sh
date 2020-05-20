#!/usr/bin/env bash
dir=`pwd`
cd $dir
mvn package
cp target/agent-1.0.0-SNAPSHOT.jar ../../src/main/resources/tools/util4jAgent.jar