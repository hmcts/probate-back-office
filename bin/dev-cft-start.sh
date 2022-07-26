#!/bin/bash

# Set variables
COMPOSE_FILE=""
BIN_FOLDER=$($(dirname "$0")/probate-dev-env-realpath)


echo "Waiting for IDAM to start - make sure you have ran ./gradlew bootWithCCD"
until curl http://localhost:5000/health
do
  echo "Waiting for IDAM";
  sleep 5;
done

# Start all other images
echo "Starting dependencies..."
docker-compose ${COMPOSE_FILE} up -d

until curl http://localhost:8991/__admin
do
  echo "Waiting for Wiremock";
  sleep 5;
done

../probate-dev-env/bin/wiremock.sh
echo "LOCAL ENVIRONMENT SUCCESSFULLY STARTED"
