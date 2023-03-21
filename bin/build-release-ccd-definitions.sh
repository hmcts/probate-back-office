#!/bin/bash

set -eu

environment=${1:-prod}

if [[ ${environment} != "prod" && ${environment} != "aat" && ${environment} != "demo" && ${environment} != "ithc" && ${environment} != "perftest" ]]; then
  echo "Environment '${environment}' is not supported!"
  exit 1
fi
.././ccdImports/conversionScripts/createAllXLS-pipeline.sh probate-back-office-${1}.service.core-compute-${1}.internal ${1} ${2}
