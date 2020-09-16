#!/bin/sh

IMPORTER_USERNAME=${1:-ccd.docker.default@hmcts.net}
IMPORTER_PASSWORD=${2:-Pa55word11}
IDAM_URI=${IDAM_API_URL:-http://localhost:5000}
REDIRECT_URI=${IDAM_REDIRECT_URL:-http://localhost:3451/oauth2redirect}
CLIENT_ID="ccd_gateway"
CLIENT_SECRET=${API_GATEWAY_IDAM_SECRET:-ccd_gateway_secret}

echo ${IMPORTER_USERNAME}
echo ${IMPORTER_PASSWORD}
echo ${IDAM_URI}
echo ${REDIRECT_URI}
echo ${CLIENT_ID}
echo ${CLIENT_SECRET}

echo $(echo curl ${CURL_OPTS} -H "Content-Type: application/x-www-form-urlencoded" -u "${CLIENT_ID}:${CLIENT_SECRET}" -XPOST "${IDAM_URI}/oauth2/token?code=${code}&redirect_uri=${REDIRECT_URI}&grant_type=authorization_code" -d "" | base64)
curl -v ${CURL_OPTS} -u "${IMPORTER_USERNAME}:${IMPORTER_PASSWORD}" -XPOST "${IDAM_URI}/oauth2/authorize?redirect_uri=${REDIRECT_URI}&response_type=code&client_id=${CLIENT_ID}" -d ""

code=$(curl ${CURL_OPTS} -u "${IMPORTER_USERNAME}:${IMPORTER_PASSWORD}" -XPOST "${IDAM_URI}/oauth2/authorize?redirect_uri=${REDIRECT_URI}&response_type=code&client_id=${CLIENT_ID}" -d "" | docker run --rm --interactive stedolan/jq -r .code)

curl ${CURL_OPTS} -H "Content-Type: application/x-www-form-urlencoded" -u "${CLIENT_ID}:${CLIENT_SECRET}" -XPOST "${IDAM_URI}/oauth2/token?code=${code}&redirect_uri=${REDIRECT_URI}&grant_type=authorization_code" -d "" | docker run --rm --interactive stedolan/jq -r .access_token
