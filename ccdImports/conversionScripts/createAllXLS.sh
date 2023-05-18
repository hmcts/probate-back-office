#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
echo conversionFolder=  $conversionFolder
configFolder=${conversionFolder}/../configFiles

if [ -z "$1" ]
  then
    echo "Usage: ./ccdImports/conversionScripts/createAllXLS.sh CCD_DEF_CASE_SERVICE_BASE_URL CCD_DEF_AAC_URL"
    exit 1
fi

echo bbb

export CCD_DEF_CASE_SERVICE_BASE_URL=$1
echo 000- $CCD_DEF_CASE_SERVICE_BASE_URL
if [ -z "2" ]
  then
    export CCD_DEF_AAC_URL=manage-case-assignment:4454
fi

echo ccc

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL

${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Backoffice/
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Caveat/
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Cases/
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Search/
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Will_Lodgement/
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Standing_Search/

echo XLS files placed in /jsonToXLS folder
