#!/usr/bin/env bash

set -eu

dir=$(dirname ${0})
filepath=${1}
filename=$(basename ${filepath})
uploadFilename="$(date +"%Y%m%d-%H%M%S")-${filename}"
importJobId=$(uuidgen | tr '[:upper:]' '[:lower:]')

echo filepath =$filepath
echo "Import job ID = ${importJobId}"

if [ -z "${USER_TOKEN:-}" ]; then
  echo Get User token
  userToken=$(${dir}/idam-lease-user-token.sh ${CCD_CONFIGURER_IMPORTER_USERNAME} ${CCD_CONFIGURER_IMPORTER_PASSWORD})
else
  echo Use cache User token
  userToken=${USER_TOKEN}
fi

if [ -z "${SERVICE_TOKEN:-}" ]; then
  echo Get Service token
  serviceToken=$(${dir}/idam-lease-service-token.sh ccd_gw $(docker run --rm hmctsprod.azurecr.io/imported/toolbelt/oathtool --totp -b ${API_GATEWAY_S2S_KEY:-AAAAAAAAAAAAAAAA}))
else
  echo Use cache Service token
  serviceToken=${SERVICE_TOKEN}
fi

ccdDefinitionStoreUrl=${CCD_DEFINITION_STORE_API_BASE_URL:-http://localhost:4451}
echo "ccdDefinitionStoreUrl = ${ccdDefinitionStoreUrl}"

poll_import_status() {
  for try in {1..10}; do
    sleep 5

    echo "Checking import job ${importJobId} status (Try ${try})"

    jobResponse=$(curl --insecure --silent --show-error \
      -X GET \
      "${ccdDefinitionStoreUrl}/import-jobs/${importJobId}" \
      -H "Authorization: Bearer ${userToken}" \
      -H "ServiceAuthorization: Bearer ${serviceToken}" || true)

    if [[ "${jobResponse}" == *"COMPLETED"* ]]; then
      echo "${filename} (${uploadFilename}) uploaded"
      exit 0
    fi

    if [[ "${jobResponse}" == *"FAILED"* ]]; then
      echo "${filename} (${uploadFilename}) upload failed: ${jobResponse}"
      exit 1
    fi

    if [[ "${jobResponse}" == *"EXPIRED"* ]]; then
      echo "${filename} (${uploadFilename}) upload expired: ${jobResponse}"
      exit 1
    fi
  done

  echo "${filename} (${uploadFilename}) upload status could not be confirmed"
  exit 1
}

max_upload_attempts=3
upload_retry_delay=5
attempt=1
upload_curl_failed=false

while true; do
  echo "Uploading ${filename} (${uploadFilename}) - attempt ${attempt}/${max_upload_attempts}"

  if uploadResponse=$(curl --insecure --silent -w "\n%{http_code}" --show-error \
    -X POST \
    "${ccdDefinitionStoreUrl}/import" \
    -H "Authorization: Bearer ${userToken}" \
    -H "ServiceAuthorization: Bearer ${serviceToken}" \
    -H "X-Import-Job-Id: ${importJobId}" \
    -F "file=@${filepath};filename=${uploadFilename}"); then

    upload_http_code=$(echo "${uploadResponse}" | tail -n1)
    upload_response_content=$(echo "${uploadResponse}" | sed '$d')
  else
    upload_curl_failed=true
    echo "Import request encountered a connection or technical error"
    break
  fi

  # Only retry if the HTTP code is 409 (Conflict). For other codes, break the loop and handle accordingly
  if [[ "${upload_http_code}" != "409" ]]; then
    break
  fi

  if [[ "${attempt}" -ge "${max_upload_attempts}" ]]; then
    break
  fi

  echo "Upload returned HTTP 409. Retrying in ${upload_retry_delay} seconds..."
  sleep "${upload_retry_delay}"
  attempt=$((attempt + 1))
done

if [[ "${upload_curl_failed}" == "true" ]]; then
  echo "Checking import job ${importJobId}"
  poll_import_status
  exit 1
fi

if [[ "${upload_http_code}" =~ ^2[0-9][0-9]$ ]]; then
  echo "${filename} (${uploadFilename}) uploaded"
  exit 0
fi

if [[ "${upload_http_code}" == "409" ]]; then
  echo "${filename} (${uploadFilename}) upload failed after ${attempt} attempts: HTTP 409 (${upload_response_content})"
  exit 1
fi

if [[ "${upload_http_code}" =~ ^4[0-9][0-9]$ ]]; then
  echo "${filename} (${uploadFilename}) upload failed: HTTP ${upload_http_code} (${upload_response_content})"
  exit 1
fi

echo "Import returned HTTP ${upload_http_code}; checking job ${importJobId}"
poll_import_status