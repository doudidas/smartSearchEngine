#!/bin/bash

mvn package
mv ./target/API.jar ./API.jar
mvn clean;
