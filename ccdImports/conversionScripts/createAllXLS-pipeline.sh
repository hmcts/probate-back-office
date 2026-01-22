#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
configFolder=${conversionFolder}/../configFiles
environment="$2"
shutterOption=${3:-false}
acc="$4"
envExcludedFilenamePatterns="$5"

if [ -z "$1" ]
  then
    echo "Usage: ./ccdImports/conversionScripts/createAllXLS.sh CCD_DEF_CASE_SERVICE_BASE_URL"
    exit 1
fi
if [ -z "$4" ]
  then
    echo "Usage: ./ccdImports/conversionScripts/createAllXLS.sh CCD_DEF_CASE_SERVICE_BASE_URL CCD_DEF_AAC_URL"
    exit 1
fi

if [ ${shutterOption} == true ]; then
  echo Creating shuttered CCD Definition
  excludedFilenamePatterns="-e *-unshutter.json"
else
  echo Creating unshuttered CCD Definition
  excludedFilenamePatterns="-e *-shutter.json"
fi
echo envExcludedFilenamePatterns = $envExcludedFilenamePatterns
excludedFilenamePatterns="$excludedFilenamePatterns  $envExcludedFilenamePatterns"

echo excludedFilenamePatterns = $excludedFilenamePatterns

export CCD_DEF_CASE_SERVICE_BASE_URL=$1
export CCD_DEF_AAC_URL=$4

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL,$CCD_DEF_AAC_URL

${conversionFolder}/convertJsonToXLS-pipeline.sh ${configFolder}/CCD_Probate_Backoffice/ ${environment} "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS-pipeline.sh ${configFolder}/CCD_Probate_Caveat/ ${environment} "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS-pipeline.sh ${configFolder}/CCD_Probate_Legacy_Cases/ ${environment} "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS-pipeline.sh ${configFolder}/CCD_Probate_Legacy_Search/ ${environment} "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS-pipeline.sh ${configFolder}/CCD_Probate_Will_Lodgement/ ${environment} "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS-pipeline.sh ${configFolder}/CCD_Probate_Standing_Search/ ${environment} "${excludedFilenamePatterns}"

echo XLS files placed in /jsonToXLS-${2} folder
