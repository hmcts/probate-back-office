#!/usr/bin/env bash

set -eu

binFolder=$(dirname "$0")/../../bin
xlsToJsonFolder=$(dirname "$0")/../../jsonToXLS

echo binFolder = $binFolder
echo xlsToJsonFolder = $xlsToJsonFolder

${binFolder}/ccd-add-all-roles.sh
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Backoffice.xlsx"
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Caveat.xlsx"
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Legacy_Cases.xlsx"
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Legacy_Search.xlsx"
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Standing_Search.xlsx"
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Will_Lodgement.xlsx"

