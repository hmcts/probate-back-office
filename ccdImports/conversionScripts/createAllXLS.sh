#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
configFolder=${conversionFolder}/../configFiles

if [ -z "$1" ]
  then
    echo "Usage: ./ccdImports/conversionScripts/createAllXLS.sh CCD_DEF_CASE_SERVICE_BASE_URL"
    exit 1
fi

export CCD_DEF_CASE_SERVICE_BASE_URL=$1

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL

${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Backoffice/
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Caveat/
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Cases/
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Search/
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Standing_Search/
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Will_Lodgement/

echo XLS files placed in /jsonToXLS folder

#${binFolder}/ccd-add-all-roles.sh
#${binFolder}/ccd-import-definition.sh "../../xlsToJson/CCD_Probate_Backoffice.xlsx"
#${binFolder}/ccd-import-definition.sh "../../xlsToJson/CCD_Probate_Caveat.xlsx"
#${binFolder}/ccd-import-definition.sh "../../xlsToJson/CCD_Probate_LegacyCases.xlsx"
#${binFolder}/ccd-import-definition.sh "../../xlsToJson/CCD_Probate_LegacySearch.xlsx"
#${binFolder}/ccd-import-definition.sh "../../xlsToJson/CCD_Probate_StandingSearch.xlsx"
#${binFolder}ccd-import-definition.sh "../../xlsToJson/CCD_Probate_WillLodgement.xlsx"

