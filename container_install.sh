#!/bin/bash
mvn package
mv ./target/API.jar ./API.jar
mvn clean
ls | grep -v -E "API.jar|smartSearch.yaml" | xargs -d "\n" rm -Rf
