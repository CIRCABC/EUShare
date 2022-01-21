#!/bin/bash


rm -rf ../client/target/eushareclient.war 
rm -rf ../server/target/eushareserver.war 


pushd ../client/angular
npm install
#ng build --configuration=dev --base-href /share/
ng build --configuration=dev --base-href /share/ 


pushd ../client
mvn clean install
popd

pushd ../server
mvn clean install -Pdev-tomcat -Dskip.unit.tests=true
popd

rm -rf ../docker/client-tomcat/dist/eushareclient.war
rm -rf ../docker/server-tomcat/dist/eushareserver.war

cp ../client/target/eushareclient.war ../docker/client-tomcat/dist/eushareclient.war
cp ../server/target/eushareserver.war ../docker/server-tomcat/dist/eushareserver.war
