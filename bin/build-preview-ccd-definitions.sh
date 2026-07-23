#!/bin/bash

set -eu

environment=${1:-prod}
shutterOption=${2:-false}
excludedFilenamePatterns=${3:-""}

env AZURE_CONFIG_DIR=/opt/jenkins/.azure-nonprod az acr login --name hmctsprod

.././ccdImports/conversionScripts/createAllXLS-pipeline.sh probate-back-office-pr-${environment}-java "preview" ${shutterOption} probate-back-office-pr-${environment}-aac-manage-case-assignment ${excludedFilenamePatterns}
