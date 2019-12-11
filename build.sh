#!/bin/bash

echo "Performing a clean Maven build"
./mvnw clean package -DskipTests=true

# echo "Building the UAA"
# cd uaa
# docker build --tag toquery/example-spring-cloud-gateway-uua .
# cd ..

echo "Building the Gateway"
cd gateway
docker build --tag toquery/example-spring-cloud-gateway .
cd ..

echo "Building the Services"
cd resource
docker build --tag toquery/example-spring-cloud-gateway-resource .
cd ..
