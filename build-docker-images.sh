#!/bin/sh

#./mvnw clean package install
./mvnw spring-boot:build-image-no-fork -pl content-checker/content-checker-service
./mvnw spring-boot:build-image-no-fork -pl email/email-service
./mvnw spring-boot:build-image-no-fork -pl trend/trend-service
./mvnw spring-boot:build-image-no-fork -pl main-app/main-webapp
./mvnw spring-boot:build-image-no-fork -pl main-app/report-service

docker image ls | grep gtapp
