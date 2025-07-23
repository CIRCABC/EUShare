#!/bin/bash


rm -rf ../client/target/eushareclient.war 
rm -rf ../docker/client-tomcat/dist/eushareclient.war
pushd ../client/angular
rm -rf dist
mkdir -p dist
npm install
ng build --configuration=dev 
popd
pushd ../client
rm -rf target
mkdir -p target
mvn clean install
popd

cp ../client/target/eushareclient.war ../docker/client-tomcat/dist/eushareclient.war



rm -rf ../server/target
mkdir -p ../server/target
rm -rf  ../docker/server-tomcat/dist/eushareserver.war
pushd ../server
mvn clean install -Pdev-tomcat -Dspring.profiles.active=dev -Dskip.unit.tests=true
popd

cp ../server/target/eushareserver.war ../docker/server-tomcat/dist/eushareserver.war


mkdir -p ~/eushare-data

docker-compose -f docker-compose-tomcat.yaml down -v
docker-compose -f docker-compose-tomcat.yaml up --force-recreate --build
