#!/bin/bash

REGISTRY=registry.cn-guangzhou.aliyuncs.com/gill-gplatform
PROJECT=$1
VERSION=$2

docker stop $PROJECT
docker rm $PROJECT 

docker rmi $REGISTRY/$PROJECT:$VERSION
