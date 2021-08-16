#!/usr/bin/env bash

set -eu

binFolder=$(dirname "$0")/../../bin
xlsToJsonFolder=$(dirname "$0")/../../jsonToXLS

echo binFolder = $binFolder
echo xlsToJsonFolder = $xlsToJsonFolder

echo CCD_Probate_Backoffice ....................
${binFolder}/ccd-import-definition.sh "${xlsToJsonFolder}/CCD_Probate_Backoffice.xlsx"
echo

