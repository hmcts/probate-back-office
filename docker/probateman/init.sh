#!/usr/bin/env bash

set -e

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=probate_man --set PASSWORD=probate_man --set DATABASE=probate_man <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';
  CREATE DATABASE :DATABASE WITH OWNER = :USERNAME ENCODING = 'UTF-8' CONNECTION LIMIT = -1;
EOSQL

psql -U probate_man -d probate_man -a -f create_tables.sql
