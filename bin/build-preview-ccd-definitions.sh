#!/bin/bash

set -eu

environment=${1:-prod}
shutterOption=${2:-false}
excludedFilenamePatterns=${3:-""}
.././ccdImports/conversionScripts/createAllXLS-pipeline.sh probate-back-office-pr-${environment}-java "preview" ${shutterOption} probate-back-office-pr-${environment}-aac-manage-case-assignment ${excludedFilenamePatterns}
