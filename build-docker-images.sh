#!/bin/sh

# full build with 5 threads
mvnd  -T 5 clean package install

# create docker images out of build jar files using spring boot and packeto
# the 'build-image-no-fork' expects the project is already built and .jar file already exists in target dir

mvnd spring-boot:build-image-no-fork -pl content-checker/content-checker-service -DskipTests=true
mvnd spring-boot:build-image-no-fork -pl email/email-service -DskipTests=true
mvnd spring-boot:build-image-no-fork -pl trend/trend-service -DskipTests=true
mvnd spring-boot:build-image-no-fork -pl main-app/main-webapp -DskipTests=true
mvnd spring-boot:build-image-no-fork -pl main-app/report-service -DskipTests=true

# list
docker image ls | grep gtapp
