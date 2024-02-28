#!/bin/bash

USERNAME=ryougishikiiiii
REGISTRY=registry.cn-guangzhou.aliyuncs.com
NAMESPACE=gill-gplatform
PROJECT=$1
VERSION=$2

docker login -u $USERNAME $REGISTRY

docker pull $REGISTRY/$NAMESPACE/$PROJECT:$VERSION
docker tag $REGISTRY/$NAMESPACE/$PROJECT:$VERSION $PROJECT:$VERSION
