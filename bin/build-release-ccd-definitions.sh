#!/bin/bash

set -eu

environment=${1:-prod}
excludedFilenamePatterns=${2:-""}

if [[ ${environment} != "prod" && ${environment} != "aat" && ${environment} != "demo" && ${environment} != "ithc" && ${environment} != "perftest" ]]; then
  echo "Environment '${environment}' is not supported!"
  exit 1
fi

.././ccdImports/conversionScripts/createAllXLS-pipeline.sh probate-back-office-${1}.service.core-compute-${1}.internal ${1} ${2} aac-manage-case-assignment-${1}.service.core-compute-${1}.internal $excludedFilenamePatterns
