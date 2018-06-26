#!/bin/bash
mvn package;
mv ./target/API.jar ./API.jar;
mv src/main/resources/smartSearch.yaml; .
mvn clean;
rm -v !("API.jar"|"smartSearch.yaml");