#!/bin/bash

set -eu

excludedFilenamePatterns=${3:-""}
.././ccdImports/conversionScripts/createAllXLS-pipeline.sh probate-back-office-pr-${1}-java "preview" ${2} probate-back-office-pr-${1}-aac-manage-case-assignment ${excludedFilenamePatterns}
