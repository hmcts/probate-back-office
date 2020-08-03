#!/bin/bash

# Set variables
COMPOSE_FILE="-f compose/dev.yml"

echo "Starting databases..."
docker-compose ${COMPOSE_FILE} up -d shared-db shared-database

echo "Starting ForgeRock..."
docker-compose ${COMPOSE_FILE} up -d fr-am fr-idm

echo "Starting IDAM..."
docker-compose ${COMPOSE_FILE} up -d sidam-api

# Set up IDAM client with services and roles
echo "Setting up IDAM client..."
sleep 30
./bin/idam-client-setup.sh

# Start all other images
echo "Starting dependencies..."
docker-compose ${COMPOSE_FILE} build
docker-compose ${COMPOSE_FILE} up -d

# Set up missing Fees keyword
echo "Setting up Feeds keyword"
until psql -h localhost --username postgres -d fees_register -p 5050 -c "UPDATE public.fee SET keyword = 'NewFee' WHERE code = 'FEE0003'";
do
  echo "Retrying";
  sleep 15;
done

# Fees API migrations appear to be broken so it fails to boot first time round
docker-compose ${COMPOSE_FILE} restart fees-api

echo "LOCAL ENVIRONMENT SUCCESSFULLY STARTED"