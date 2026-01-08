#!/usr/bin/env bash
set -eu

# LOCALLY:
#CHANGE_ID=4357
#IDAM_DATA_STORE_SYSTEM_USER_USERNAME=$(az keyvault secret show --vault-name ccd-aat -o tsv --query value --name idam-data-store-system-user-username)
#IDAM_DATA_STORE_SYSTEM_USER_PASSWORD=$(az keyvault secret show --vault-name ccd-aat -o tsv --query value --name idam-data-store-system-user-password)
#export CCD_API_GATEWAY_IDAM_CLIENT_SECRET=$(az keyvault secret show --vault-name ccd-aat -o tsv --query value --name ccd-api-gateway-oauth2-client-secret)

BASEDIR=$(dirname "$0")
IDAM_TOKEN=$(${BASEDIR}/../idam-lease-user-token.sh $IDAM_DATA_STORE_SYSTEM_USER_USERNAME $IDAM_DATA_STORE_SYSTEM_USER_PASSWORD)
S2S_TOKEN=$(${BASEDIR}/../s2s-token.sh "am_org_role_mapping_service")

[ -z "$S2S_TOKEN" ] && >&2 echo "No service token" && exit
[ -z "$IDAM_TOKEN" ] && >&2 echo "No user token" && exit

function send_curl_request() {
  local json_file=$1
  local user_type=$2

  if [[ ! -f "${json_file}" ]]; then
    echo "File not found: ${json_file}"
    return 1
  fi

  local payload=$(cat "${json_file}")
  local url="https://probate-back-office-am-org-role-mapping-service-pr-${CHANGE_ID}.preview.platform.hmcts.net/am/testing-support/createOrgMapping?userType=${user_type}"

  curl --silent --show-error --fail "${url}" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${IDAM_TOKEN}" \
  -H "ServiceAuthorization: ${S2S_TOKEN}" \
  -d "${payload}"
}

send_curl_request "${BASEDIR}/aat-caseworker-user-ids.json" "CASEWORKER"
# send_curl_request "${BASEDIR}/aat-judicial-user-ids.json" "JUDICIAL"
