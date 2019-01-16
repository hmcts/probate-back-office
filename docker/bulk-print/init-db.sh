#!/usr/bin/env bash

set -e

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=letterservice --set PASSWORD=password <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';

  CREATE DATABASE letter_tracking
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
EOSQL
