#!/bin/bash

docker rmi -f eushare-dev-client
docker rmi -f eushare-dev-server

pushd ../client/angular
rm -rf ../docker/client/dist
mkdir -p ../docker/client/dist
cp -rf . ../../docker/client/dist/
popd

pushd ../server
rm -rf ../docker/server/dist
mkdir -p ../docker/server/dist
cp -r . ../docker/server/dist/server
cp ../api.yaml ../docker/server/dist/api.yaml
popd

mkdir -p ~/eushare-data

docker-compose -f docker-compose.yaml down -v
docker-compose -f docker-compose.yaml up --force-recreate --build
