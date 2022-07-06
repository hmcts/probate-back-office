#!/bin/bash

# Set variables
COMPOSE_FILE="-f docker-compose-cft.yml"


echo "Logging into ACR..."
az acr login --name hmctspublic --subscription 8999dec3-0104-4a27-94ee-6588559729d1
az acr login --name hmctsprivate --subscription 8999dec3-0104-4a27-94ee-6588559729d1

echo "Pulling docker images..."
docker-compose ${COMPOSE_FILE} pull
docker-compose ${COMPOSE_FILE} build

# Set up IDAM client with services and roles
echo "Setting up IDAM client..."
until curl http://localhost:5000/health
do
  echo "Waiting for IDAM";
  sleep 10;
done

#$BIN_FOLDER/idam-client-setup.sh

# Start all other images
echo "Starting dependencies..."
docker-compose ${COMPOSE_FILE} up -d fees-api

# Fees API migrations appear to be broken so it fails to boot first time round
docker-compose ${COMPOSE_FILE} restart fees-api

#echo "Setting up CCD roles..."
until curl http://localhost:4451/health
do
  echo "Waiting for CCD";
  sleep 10;
done

#$BIN_FOLDER/ccd-add-all-roles.sh
#$BIN_FOLDER/../ccdImports/conversionScripts/createAllXLS.sh probate-back-office:4104
#$BIN_FOLDER/../ccdImports/conversionScripts/importAllXLS.sh

#docker-compose ${COMPOSE_FILE} stop

echo "CFT LOCAL ENVIRONMENT SUCCESSFULLY UP"

