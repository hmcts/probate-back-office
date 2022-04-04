#!/bin/sh

IDAM_URI="${IDAM_API_BASE_URL:-http://localhost:5000}"
role=$1

if [ -z "$role" ]
  then
    echo "Usage: ./idam-role.sh role [role] [description]"
    exit 1
fi
authToken=$(curl -s -X POST "${IDAM_URI}/loginUser" -H "accept: application/json" -H "Content-Type: application/x-www-form-urlencoded" -d "password=Ref0rmIsFun&username=idamOwner@hmcts.net" | docker run --rm --interactive stedolan/jq -r .api_auth_token)

curl --request POST \
  --url "${IDAM_URI}/roles" \
  --header "Authorization: AdminApiAuthToken ${authToken}" \
  --header "Content-Type: application/json" \
  --data '{"id": "'${role}'","name": "'${role}'","description": "'${role}'","assignableRoles": [],"conflictingRoles": []}'