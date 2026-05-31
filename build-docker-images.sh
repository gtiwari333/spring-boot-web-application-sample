#!/bin/sh

# create docker images out of build jar files using spring boot and packeto

mvnd spring-boot:build-image -pl content-checker/content-checker-service -DskipTests=true
mvnd spring-boot:build-image -pl email/email-service -DskipTests=true
mvnd spring-boot:build-image -pl trend/trend-service -DskipTests=true
mvnd spring-boot:build-image -pl main-app/main-webapp -DskipTests=true
mvnd spring-boot:build-image -pl main-app/report-service -DskipTests=true

# list
docker image ls | grep gtapp
