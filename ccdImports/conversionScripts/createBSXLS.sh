#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
configFolder=${conversionFolder}/../configFiles

if [ -z "$1" ]
  then
    echo "Usage: ./ccdImports/conversionScripts/createBSXLS.sh CCD_DEF_CASE_SERVICE_BASE_URL"
    exit 1
fi

export CCD_DEF_CASE_SERVICE_BASE_URL=$1

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL

${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_BulkScanning_ExceptionRecord/

echo XLS files placed in /jsonToXLS folder

#${binFolder}/ccd-add-all-roles.sh
#${binFolder}/ccd-import-definition.sh "../../xlsToJson/CCD_Probate_BulkScanning_ExceptionRecord.xlsx"
