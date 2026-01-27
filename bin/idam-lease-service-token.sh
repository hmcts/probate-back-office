#!/usr/bin/env bash

set -xeu

microservice=${1}
oneTimePassword=${2}

s2sUrl=${S2S_URL_BASE:-http://rpe-service-auth-provider-aat.service.core-compute-aat.internal}/testing-support/lease

if [[ "${ENV}" == 'prod' ]]; then
  s2sUrl=${S2S_URL_BASE:-http://rpe-service-auth-provider-aat.service.core-compute-aat.internal}/lease
fi

curl -v --insecure --fail --show-error --silent -X POST \
  ${s2sUrl} \
  -H "Content-Type: application/json" \
  -d '{
    "microservice": "'${microservice}'",
    "oneTimePassword": "'${oneTimePassword}'"
  }'
