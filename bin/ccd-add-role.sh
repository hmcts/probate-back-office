#!/bin/bash
## Usage: ./ccd-add-role.sh role [classification]
##
## Options:
##    - role: Name of the role. Must be an existing IDAM role.
##    - classification: Classification granted to the role; one of `PUBLIC`,
##        `PRIVATE` or `RESTRICTED`. Default to `PUBLIC`.
##
## Add support for an IDAM role in CCD.

role=$1
classification=${2:-PUBLIC}

if [ -z "$role" ]
  then
    echo "Usage: ./ccd-add-role.sh role [classification]"
    exit 1
fi

case $classification in
  PUBLIC|PRIVATE|RESTRICTED)
    ;;
  *)
    echo "Classification must be one of: PUBLIC, PRIVATE or RESTRICTED"
    exit 1 ;;
esac

binFolder=$(dirname "$0")

if [ -z "$USER_TOKEN_ENV" ]; then
  echo "User token not present"
  export USER_TOKEN_ENV=$(${binFolder}/idam-lease-user-token.sh ${CCD_CONFIGURER_IMPORTER_USERNAME:-ccd.docker.default@hmcts.net} ${CCD_CONFIGURER_IMPORTER_PASSWORD:-Pa55word11})
fi

if [ -z "$SERVICE_TOKEN_ENV" ]; then
  echo "Service token not present"
  export SERVICE_TOKEN_ENV=$(${binFolder}/idam-lease-service-token.sh ccd_gw $(docker run --rm hmctsprod.azurecr.io/imported/toolbelt/oathtool --totp -b ${API_GATEWAY_S2S_KEY:-AAAAAAAAAAAAAAAA}))
fi

ccdUrl=${CCD_DEFINITION_STORE_API_BASE_URL:-http://localhost:4451}

echo "Creating CCD role: ${role}"

curl --insecure --fail --show-error --silent --output /dev/null -X PUT \
  ${CCD_DEFINITION_STORE_API_BASE_URL:-http://localhost:4451}/api/user-role \
  -H "Authorization: Bearer ${USER_TOKEN_ENV}" \
  -H "ServiceAuthorization: Bearer ${SERVICE_TOKEN_ENV}" \
  -H "Content-Type: application/json" \
  -d '{
    "role": "'${role}'",
    "security_classification": "'${classification}'"
  }'
