#!/usr/bin/env bash

set -eu

binFolder=$(dirname "$0")/../../bin
xlsToJsonFolder=$(dirname "$0")/../../jsonToXLS

echo binFolder = $binFolder
echo xlsToJsonFolder = $xlsToJsonFolder

echo CCD_Probate_Backoffice ....................
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Backoffice.xlsx"
echo
echo CCD_Probate_Caveat ........................
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Caveat.xlsx"
echo
echo CCD_Probate_Legacy_Cases ..................
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Legacy_Cases.xlsx"
echo
echo CCD_Probate_Legacy_Search .................
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Legacy_Search.xlsx"
echo
echo CCD_Probate_Will_Lodgement ................
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Will_Lodgement.xlsx"
echo
echo CCD_Probate_Standing_Search ...............
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Standing_Search.xlsx"
echo