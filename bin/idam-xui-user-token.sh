#!/bin/sh

IMPORTER_USERNAME=${1:-ccd.docker.default@hmcts.net}
IMPORTER_PASSWORD=${2:-Pa55word11}
IDAM_URI="http://localhost:5000"
REDIRECT_URI="http://localhost:3455/oauth2/callback"
CLIENT_ID="xui_webapp"
CLIENT_SECRET="xui_webapp_secret"

code=$(curl ${CURL_OPTS} -u "${IMPORTER_USERNAME}:${IMPORTER_PASSWORD}" -XPOST "${IDAM_URI}/oauth2/authorize?redirect_uri=${REDIRECT_URI}&response_type=code&client_id=${CLIENT_ID}" -d "" | docker run --rm --interactive stedolan/jq -r .code)

curl ${CURL_OPTS} -H "Content-Type: application/x-www-form-urlencoded" -u "${CLIENT_ID}:${CLIENT_SECRET}" -XPOST "${IDAM_URI}/oauth2/token?code=${code}&redirect_uri=${REDIRECT_URI}&grant_type=authorization_code" -d "" | docker run --rm --interactive stedolan/jq -r .access_token
