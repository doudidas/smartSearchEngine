#!/bin/bash
docker build -t spacelama/api:latest .;
docker push spacelama/api:latest;
