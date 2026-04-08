#!/bin/sh

IMPORTER_USERNAME=${IMPORTER_USERNAME:-ccd.docker.default@hmcts.net}
IMPORTER_PASSWORD=${IMPORTER_PASSWORD:-Pa55word11}
IDAM_URI=${IDAM_API_URL:-http://localhost:5000}
REDIRECT_URI=${CCD_IDAM_REDIRECT_URL:-http://localhost:3451/oauth2redirect}
CLIENT_ID="ccd_gateway"
CLIENT_SECRET=${API_GATEWAY_IDAM_SECRET:-ccd_gateway_secret}

curl "${CURL_OPTS}" --silent --show-error --fail \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -XPOST "${IDAM_URI}/o/token" \
    --data-urlencode "grant_type=password" \
    --data-urlencode "client_id=${CLIENT_ID}" \
    --data-urlencode "client_secret=${CLIENT_SECRET}" \
    --data-urlencode "scope=openid profile roles" \
    --data-urlencode "username=${IMPORTER_USERNAME}" \
    --data-urlencode "password=${IMPORTER_PASSWORD}" \
    --data-urlencode "redirect_uri=${REDIRECT_URI}" | docker run --rm --interactive hmctsprod.azurecr.io/imported/jqlang/jq -r .access_token