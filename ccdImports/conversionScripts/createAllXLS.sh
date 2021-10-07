#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
configFolder=${conversionFolder}/../configFiles
environment="$2"

if [ -z "$1" ]
  then
    echo "Usage: ./ccdImports/conversionScripts/createAllXLS.sh CCD_DEF_CASE_SERVICE_BASE_URL"
    exit 1
fi

export CCD_DEF_CASE_SERVICE_BASE_URL=$1

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL

${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Backoffice/ ${environment}
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Caveat/ ${environment}
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Cases/ ${environment}
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Search/ ${environment}
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Will_Lodgement/ ${environment}
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Standing_Search/ ${environment}

echo XLS files placed in /jsonToXLS folder
