#!/bin/bash

set -eu

.././ccdImports/conversionScripts/createAllXLS-pipeline.sh probate-back-office-pr-${1}-java "preview" ${2} probate-back-office-pr-${1}-aac-manage-case-assignment
