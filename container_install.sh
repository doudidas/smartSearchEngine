#!/bin/bash
mvn package
mv /usr/src/myapp/target/API.jar /usr/src/myapp/API.jar
mvn clean
ls | grep -v -E "API.jar|smartSearch.yaml" | xargs -d "\n" rm -f
