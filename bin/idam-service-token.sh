#!/bin/bash
## Usage: ./idam-service-token.sh [microservice_name]
##
## Options:
##    - microservice_name: Name of the microservice. Default to `ccd_gw`.
##
## Returns a valid IDAM service token for the given microservice.


MICROSERVICE="${1:-ccd_gw}"

curl --silent -X POST http://localhost:4502/testing-support/lease -d '{"microservice":"probate_backend"}' -H "CONTENT-TYPE:application/json"

