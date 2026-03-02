#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
configFolder=${conversionFolder}/../configFiles
# Params:
#   $1 = CCD_DEF_CASE_SERVICE_BASE_URL
#   $2 = CCD_DEF_AAC_URL
#   $3 = shutterOption (true|false), optional, defaults to false
caseServiceUrl="${1-}"
aacUrl="${2-}"
shutterOption="${3:-false}"
extraExclusions=${4:-",*-wa.json"}

waEnabledVar=${PROBATE_WA_ENABLED:-false}
if [ ${waEnabledVar} == true ]; then
  extraExclusions=""
fi

if [ -z "$1" ]
  then
    echo "Usage: ./ccdImports/conversionScripts/createAllXLS.sh CCD_DEF_CASE_SERVICE_BASE_URL CCD_DEF_AAC_URL"
    exit 1
fi

if [ ${shutterOption} == true ]; then
  echo Creating shuttered CCD Definition
  excludedFilenamePatterns="-e *-unshutter.json$extraExclusions"
else
  echo Creating unshuttered CCD Definition
  excludedFilenamePatterns="-e *-shutter.json$extraExclusions"
fi
echo excludedFilenamePatterns = $excludedFilenamePatterns

export CCD_DEF_CASE_SERVICE_BASE_URL="${caseServiceUrl}"
export CCD_DEF_AAC_URL="${aacUrl}"

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL,$CCD_DEF_AAC_URL

${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Backoffice/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Caveat/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Cases/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Search/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Will_Lodgement/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Standing_Search/ "${excludedFilenamePatterns}"

echo XLS files placed in /jsonToXLS folder
