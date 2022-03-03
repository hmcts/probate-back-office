#!/bin/sh
binFolder=$(dirname "$0")
(${binFolder}/idam-role.sh caseworker-caa)

(${binFolder}/ccd-add-role.sh caseworker-caa)
(${binFolder}/xui-add-role.sh caseworker-caa)

# update the services to have the new role
IDAM_URI="http://localhost:5000"
authToken=$(curl -H 'Content-Type: application/x-www-form-urlencoded' -XPOST "${IDAM_URI}/loginUser?username=idamOwner@hmcts.net&password=Ref0rmIsFun" | jq -r .api_auth_token)
echo
echo authToken=$authToken
echo

# Assign all the roles to the ccd_gateway client
curl -XPUT \
  ${IDAM_URI}/services/ccd_gateway/roles \
 -H "Authorization: AdminApiAuthToken ${authToken}" \
 -H "Content-Type: application/json" \
 -d '["ccd-import", "caseworker", "caseworker-probate", "caseworker-probate", "caseworker-probate-issuer", "caseworker-probate-solicitor", "caseworker-probate-authoriser", "caseworker-probate-systemupdate", "caseworker-probate-caseofficer", "caseworker-probate-caseadmin", "caseworker-probate-registrar", "caseworker-probate-superuser", "caseworker-probate-charity", "caseworker-probate-scheduler", "payment", "caseworker-caa"]'
echo
echo FINISHED ccd_gateway
echo

# Assign roles to the xui_webapp client
curl -XPUT \
  ${IDAM_URI}/services/xui_webapp/roles \
 -H "Authorization: AdminApiAuthToken ${authToken}" \
 -H "Content-Type: application/json" \
 -d '["ccd-import", "caseworker", "caseworker-probate", "caseworker-probate-solicitor", "caseworker-probate-superuser", "pui-case-manager", "pui-user-manager", "caseworker-caa"]'
echo
echo FINISHED xui_webapp
echo

(${binFolder}/idam-create-caseworker.sh caseworker,caseworker-caa,pui-case-manager,pui-user-manager caa-caseworker@mailnesia.com Password12 caa caseworker)
echo
echo FINISHED idam-create-caseworker
echo

(${binFolder}/idam-create-service.sh "aac_manage_case_assignment" "aac_manage_case_assignment" "AAAAAAAAAAAAAAAA" "https://manage-case-assignment/oauth2redirect" "false" "profile openid roles manage-user")
echo
echo FINISHED idam-create-service.sh
echo

#put user1 and 2 userId in here
(${binFolder}/wiremock.sh "ba2bc5d7-c8ea-4e5b-bb2a-9ce75a00855c" "c4260a34-014d-41d2-b07a-1ad97482a958")
echo
echo FINISHED wiremock
echo

