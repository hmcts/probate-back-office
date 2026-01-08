#!/usr/bin/env bash

set -xe

USERNAME=${1}
PASSWORD=${2}

#echo "IDAM_API_URL_BASE": ${IDAM_API_URL_BASE}
IDAM_URI=https://idam-api.aat.platform.hmcts.net
REDIRECT_URI=http://localhost:3451/oauth2redirect
CLIENT_ID="ccd_gateway"
CLIENT_SECRET=${CCD_API_GATEWAY_IDAM_CLIENT_SECRET:-${CCD_API_GATEWAY_OAUTH2_CLIENT_SECRET:-ccd_gateway_secret}}
SCOPE="openid%20profile%20roles"

curl --silent --show-error --fail \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -XPOST "${IDAM_URI}/o/token?grant_type=password&redirect_uri=${REDIRECT_URI}&client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&username=${USERNAME}&password=${PASSWORD}&scope=${SCOPE}" -d "" | jq -r .access_token
