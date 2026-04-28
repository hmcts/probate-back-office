#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
configFolder=${conversionFolder}/../configFiles
# Params:
#   $1 = CCD_DEF_CASE_SERVICE_BASE_URL
#   $2 = CCD_DEF_AAC_URL
#   $3 = shutterOption (true|false), optional, defaults to false
#   $4 = extraExclusions (optional, comma-separated patterns) Additional exclusions are appended based on feature flags:
#        - WA disabled → adds "*-wa.json"
#        - GS disabled → adds "*-gs.json"
caseServiceUrl="${1-}"
aacUrl="${2-}"
shutterOption="${3:-false}"
extraExclusions="${4:-}"

waEnabledVar=${PROBATE_WA_ENABLED:-false}
gsEnabledVar=${PROBATE_GS_ENABLED:-false}

if [ -z "$caseServiceUrl" ] || [ -z "$aacUrl" ]; then
    echo "Usage: ./ccdImports/conversionScripts/createAllXLS.sh CCD_DEF_CASE_SERVICE_BASE_URL CCD_DEF_AAC_URL"
    exit 1
fi

patterns=()

echo "[INFO] Initial extraExclusions: $extraExclusions"
# WA flag
if [[ "${waEnabledVar}" != true ]]; then
  echo "[INFO] WA feature is DISABLED adding *-wa.json to exclusions"
  patterns+=("*-wa.json")
else
  echo "[INFO] WA feature is ENABLED no exclusion added"
fi

# GS flag
if [[ "${gsEnabledVar}" != true ]]; then
  echo "[INFO] GS feature is DISABLED adding *-gs.json to exclusions"
  patterns+=("*-gs.json")
else
  echo "[INFO] GS feature is ENABLED no exclusion added"
fi

# Build extraExclusions as a comma-separated string from the patterns array.
# - If patterns is empty - extraExclusions remains an empty string ("")
# - If patterns has values - join them with commas and prefix with a leading comma
#   e.g. ["*-wa.json","*-gs.json"] - ",*-wa.json,*-gs.json"
if [ ${#patterns[@]} -gt 0 ]; then
  extraExclusions=",$(IFS=,; printf "%s" "${patterns[*]}")"
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

export CCD_DEF_CASE_SERVICE_BASE_URL="${caseServiceUrl}"
export CCD_DEF_AAC_URL="${aacUrl}"

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL,$CCD_DEF_AAC_URL

${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Backoffice/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Caveat/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Cases/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Search/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Will_Lodgement/ "${excludedFilenamePatterns}"
${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Standing_Search/ "${excludedFilenamePatterns}"

echo XLS files placed in /jsonToXLS folder
