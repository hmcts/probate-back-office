#!/bin/bash

set -eu

environment=${1:-prod1}

if [[ ${environment} != "prod1" && ${environment} != "aat" && ${environment} != "demo" && ${environment} != "ithc" && ${environment} != "perftest" ]]; then
  echo "Environment '${environment}' is not supported!"
  exit 1
fi

if [[ ${environment} == "prod1" ]]; then
  excludedFilenamePatterns="-e UserProfile.json,*-nonprod.json,*-testing.json"
else
  excludedFilenamePatterns="-e UserProfile.json,*-prod.json"
fi

.././ccdImports/conversionScripts/createAllXLS-pipeline.sh probate-back-office-${1}.service.core-compute-${1}.internal ${1}
#./ccdImports/conversionScripts/importAllXLS.sh "${environment}"
