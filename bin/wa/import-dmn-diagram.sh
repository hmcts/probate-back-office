#!/usr/bin/env bash

set -xeu
workspace=${1}
env=${2}
tenant_id=${3}
product=${4}

s2sSecret=${S2S_SECRET:-AABBCCDDEEFFGGHH}

if [[ "${ENV}" == 'prod' ]]; then
  s2sSecret=${S2S_SECRET_PROD}
fi

serviceToken=$($(realpath $workspace)/bin/utils/idam-lease-service-token.sh probate_backend \
  $(docker run --rm toolbelt/oathtool --totp -b ${s2sSecret}))

dmnFilepath="$(realpath $workspace)/src/main/resources/dmn"

for file in $(find ${dmnFilepath} -name '*.dmn')
do
  uploadResponse=$(curl --insecure -v --silent -w "\n%{http_code}" --show-error -X POST \
    ${CAMUNDA_BASE_URL:-http://camunda-api-aat.service.core-compute-aat.internal}/engine-rest/deployment/create \
    -H "Accept: application/json" \
    -H "ServiceAuthorization: Bearer ${serviceToken}" \
    -F "deployment-name=$(basename ${file})" \
    -F "deploy-changed-only=true" \
    -F "deployment-source=$product" \
    ${tenant_id:+'-F' "tenant-id=$tenant_id"} \
    -F "file=@${dmnFilepath}/$(basename ${file})")

upload_http_code=$(echo "$uploadResponse" | tail -n1)
upload_response_content=$(echo "$uploadResponse" | sed '$d')

if [[ "${upload_http_code}" == '200' ]]; then
  echo "$(basename ${file}) diagram uploaded successfully (${upload_response_content})"
  continue;
fi

echo "$(basename ${file}) upload failed with http code ${upload_http_code} and response (${upload_response_content})"
continue;

done
