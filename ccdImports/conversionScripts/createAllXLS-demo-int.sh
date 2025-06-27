#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
configFolder=${conversionFolder}/../configFiles/demo_int_specific
shutterOption=${2:-false}

if [ -z "$1" ]
  then
    echo "Usage: ./ccdImports/conversionScripts/createAllXLS-demo-int.sh CCD_DEF_CASE_SERVICE_BASE_URL CCD_DEF_AAC_URL"
    exit 1
fi

if [ ${shutterOption} == true ]; then
  echo Creating shuttered CCD Definition
  excludedFilenamePatterns="-e *-unshutter.json"
else
  echo Creating unshuttered CCD Definition
  excludedFilenamePatterns="-e *-shutter.json"
fi
echo excludedFilenamePatterns = $excludedFilenamePatterns

export CCD_DEF_CASE_SERVICE_BASE_URL=$1
export CCD_DEF_AAC_URL=$2
echo CCD_DEF_AAC_URL=$CCD_DEF_AAC_URL

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL

${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Backoffice_demo_int/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Caveat/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Cases/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Search/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Will_Lodgement/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Standing_Search/ "${excludedFilenamePatterns}"

echo XLS files placed in /jsonToXLS_demo_int folder
