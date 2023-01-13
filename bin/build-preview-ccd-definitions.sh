#!/bin/bash

set -eu

.././ccdImports/conversionScripts/createAllXLS-pipeline.sh probate-back-office-pr-${1}-java http://aac-manage-case-assignment-aat.service.core-compute-aat.internal "preview"
