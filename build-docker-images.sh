#!/bin/sh

# full build with 5 threads
./mvnw  -T 5 clean package install

# create docker images out of build jar files using spring boot and packeto
./mvnw spring-boot:build-image-no-fork -pl content-checker/content-checker-service
./mvnw spring-boot:build-image-no-fork -pl email/email-service
./mvnw spring-boot:build-image-no-fork -pl trend/trend-service
./mvnw spring-boot:build-image-no-fork -pl main-app/main-webapp
./mvnw spring-boot:build-image-no-fork -pl main-app/report-service

# list
docker image ls | grep gtapp
