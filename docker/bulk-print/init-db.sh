#!/usr/bin/env bash

set -e

# Claim Store database
if [ -z "$LETTER_TRACKING_DB_PASSWORD" ]; then
  echo "ERROR: Missing environment variables. Set value for 'LETTER_TRACKING_DB_PASSWORD'."
  exit 1
fi

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=letterservice --set PASSWORD=password <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';

  CREATE DATABASE letter_tracking
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
EOSQL
