#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
configFolder=${conversionFolder}/../configFiles
shutterOption=${2:-false}
extraExclusions=${3:-""}

waEnabledVar=${PROBATE_WA_ENABLED:-false}
gsEnabledVar=${PROBATE_GS_ENABLED:-false}

append_exclusion() {
  if [ -z "$extraExclusions" ]; then
    extraExclusions=",$1"
  else
    extraExclusions="${extraExclusions},$1"
  fi
}

echo "[INFO] Initial extraExclusions: $extraExclusions"

# WA flag
if [[ "${waEnabledVar}" != true ]]; then
  echo "[INFO] WA feature is DISABLED adding *-wa.json to exclusions"
  append_exclusion "*-wa.json"
else
  echo "[INFO] WA feature is ENABLED no exclusion added"
fi

# GS flag
if [[ "${gsEnabledVar}" != true ]]; then
  echo "[INFO] GS feature is DISABLED adding *-gs.json to exclusions"
  append_exclusion "*-gs.json"
else
  echo "[INFO] GS feature is ENABLED no exclusion added"
fi

echo "[INFO] Final extraExclusions: $extraExclusions"

if [ ${shutterOption} == true ]; then
  echo Creating shuttered CCD Definition
  excludedFilenamePatterns="-e *-unshutter.json$extraExclusions"
else
  echo Creating unshuttered CCD Definition
  excludedFilenamePatterns="-e *-shutter.json$extraExclusions"
fi
echo excludedFilenamePatterns = $excludedFilenamePatterns

export CCD_DEF_CASE_SERVICE_BASE_URL=$1
export CCD_DEF_AAC_URL=$2
echo CCD_DEF_AAC_URL=$CCD_DEF_AAC_URL

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL

${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Backoffice/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Caveat/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Cases/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Search/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Will_Lodgement/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Standing_Search/ "${excludedFilenamePatterns}"

echo XLS files placed in /jsonToXLS folder
