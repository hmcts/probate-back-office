#!/bin/sh

IMPORTER_USERNAME=${1}
IMPORTER_PASSWORD=${2}
IDAM_URI="http://localhost:5000"
REDIRECT_URI="http://localhost:3455/oauth2/callback"
CLIENT_ID="xui_webapp"
CLIENT_SECRET="xui_webapp_secret"

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