#!/usr/bin/env bash

set -eu

binFolder=$(dirname "$0")/../../bin
xlsToJsonFolder=$(dirname "$0")/../../jsonToXLS

echo binFolder = $binFolder
echo xlsToJsonFolder = $xlsToJsonFolder

${binFolder}/ccd-add-all-roles.sh
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder)/CCD_Probate_Backoffice.xlsx"
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder)/CCD_Probate_Caveat.xlsx"
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder)/CCD_Probate_LegacyCases.xlsx"
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder)/CCD_Probate_LegacySearch.xlsx"
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder)/CCD_Probate_StandingSearch.xlsx"
${binFolder}ccd-import-definition.sh "${xlsToJsonFolder)/xlsToJson/CCD_Probate_WillLodgement.xlsx"

