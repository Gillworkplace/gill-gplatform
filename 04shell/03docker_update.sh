#!/bin/bash

PROJECT=$1
VERSION=$2

sh 02docker_rm.sh $PROJECT $VERSION
sh 01docker_pull.sh $PROJECT $VERSION
cd $PROJECT
sh docker_run.sh
cd ..
