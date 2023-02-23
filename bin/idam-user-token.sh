#!/bin/sh

#IMPORTER_USERNAME=${IMPORTER_USERNAME:-servicesatcdm+probate@gmail.com}
#IMPORTER_PASSWORD=${IMPORTER_PASSWORD:-Probate20}
#IDAM_URI=${IDAM_API_URL:-https://idam-api.aat.platform.hmcts.net}
#REDIRECT_URI=${CCD_IDAM_REDIRECT_URL:-https://ccd-case-management-web-aat.service.core-compute-aat.internal/oauth2redirect}
#CLIENT_SECRET=${API_GATEWAY_IDAM_SECRET:-vUstam6brAsT38ranuwRut65rakec4u6}

IMPORTER_USERNAME=${IMPORTER_USERNAME:-ccd.docker.default@hmcts.net}
IMPORTER_PASSWORD=${IMPORTER_PASSWORD:-Pa55word11}
IDAM_URI=${IDAM_API_URL:-http://localhost:5000}
REDIRECT_URI=${CCD_IDAM_REDIRECT_URL:-http://localhost:3451/oauth2redirect}
CLIENT_ID="ccd_gateway"
CLIENT_SECRET=${API_GATEWAY_IDAM_SECRET:-ccd_gateway_secret}

code=$(curl ${CURL_OPTS} -u "${IMPORTER_USERNAME}:${IMPORTER_PASSWORD}" -XPOST "${IDAM_URI}/oauth2/authorize?redirect_uri=${REDIRECT_URI}&response_type=code&client_id=${CLIENT_ID}" -d "" | docker run --rm --interactive stedolan/jq -r .code)

curl ${CURL_OPTS} -H "Content-Type: application/x-www-form-urlencoded" -u "${CLIENT_ID}:${CLIENT_SECRET}" -XPOST "${IDAM_URI}/oauth2/token?code=${code}&redirect_uri=${REDIRECT_URI}&grant_type=authorization_code" -d "" | docker run --rm --interactive stedolan/jq -r .access_token
