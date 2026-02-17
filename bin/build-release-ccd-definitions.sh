#!/bin/bash

set -eu

environment=${1:-prod}
shutterOption=${2:-false}
excludedFilenamePatterns=${3:-""}

if [[ ${environment} != "prod" && ${environment} != "aat" && ${environment} != "demo" && ${environment} != "ithc" && ${environment} != "perftest" ]]; then
  echo "Environment '${environment}' is not supported!"
  exit 1
fi

.././ccdImports/conversionScripts/createAllXLS-pipeline.sh probate-back-office-${environment}.service.core-compute-${environment}.internal ${environment} ${shutterOption} aac-manage-case-assignment-${environment}.service.core-compute-${environment}.internal $excludedFilenamePatterns
