#!/usr/bin/env bash

set -eu

conversionFolder=$(dirname "$0")
configFolder=${conversionFolder}/../configFiles
ccdDefAacUrl=${2:-""}
exclusions=${3:-""}
shutterOption=${4:-false}

if [ -z "$1" ]
  then
    echo "Usage: ./ccdImports/conversionScripts/createAllXLS.sh CCD_DEF_CASE_SERVICE_BASE_URL CCD_DEF_AAC_URL"
    exit 1
fi

echo shutterOption=${shutterOption}
if [ ${exclusions} ]; then
  exclusions=",${exclusions}"
fi
echo exclusions=${exclusions}

if [ ${shutterOption} == true ]; then
  echo Creating shuttered CCD Definition
  excludedFilenamePatterns="-e *-unshutter.json${exclusions}"
else
  echo Creating unshuttered CCD Definition
  excludedFilenamePatterns="-e *-shutter.json${exclusions}"
fi
echo excludedFilenamePatterns = $excludedFilenamePatterns

export CCD_DEF_CASE_SERVICE_BASE_URL=$1
export CCD_DEF_AAC_URL=$ccdDefAacUrl
echo CCD_DEF_AAC_URL=$CCD_DEF_AAC_URL

echo using url = $CCD_DEF_CASE_SERVICE_BASE_URL

${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Backoffice/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Caveat/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Cases/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Legacy_Search/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Will_Lodgement/ "${excludedFilenamePatterns}"
#${conversionFolder}/convertJsonToXLS.sh ${configFolder}/CCD_Probate_Standing_Search/ "${excludedFilenamePatterns}"

echo XLS files placed in /jsonToXLS folder
