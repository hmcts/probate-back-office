#!/bin/bash

set -e
set -u

if [ -z "$DB_USERNAME" ] || [ -z "$DB_PASSWORD" ]; then
  echo "ERROR: Missing environment variable. Set value for both 'DB_USERNAME' and 'DB_PASSWORD'."
  exit 1
fi

function create_user() {
    psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=$DB_USERNAME --set PASSWORD=$DB_PASSWORD <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';
EOSQL
}

function create_database() {
	local database=$1
	echo "  Creating user and database '$database'"
  psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=$DB_USERNAME --set PASSWORD=$DB_PASSWORD --set DATABASE=$database <<-EOSQL
  CREATE DATABASE :DATABASE
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
EOSQL
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
	create_user
	echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
	for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
		create_database $db
	done
	echo "Multiple databases created"
fi