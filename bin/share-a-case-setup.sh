#!/bin/bash
binFolder=$(dirname "$0")
(${binFolder}/idam-role.sh caseworker-caa)
(${binFolder}/idam-role.sh pui-caa)
(${binFolder}/idam-role.sh pui-organisation-manager)
(${binFolder}/ccd-add-role.sh caseworker-caa)
(${binFolder}/xui-add-role.sh caseworker-caa)
(${binFolder}/xui-add-role.sh pui-caa)
(${binFolder}/xui-add-role.sh pui-organisation-manager)

# update the services to have the new role
IDAM_URI="http://localhost:5000"
authToken=$(curl -H 'Content-Type: application/x-www-form-urlencoded' -XPOST "${IDAM_URI}/loginUser?username=idamOwner@hmcts.net&password=Ref0rmIsFun" | jq -r .api_auth_token)
# Assign all the roles to the ccd_gateway client
curl -XPUT \
  ${IDAM_URI}/services/ccd_gateway/roles \
 -H "Authorization: AdminApiAuthToken ${authToken}" \
 -H "Content-Type: application/json" \
 -d '["ccd-import", "caseworker", "caseworker-probate", "caseworker-probate", "caseworker-probate-issuer", "caseworker-probate-solicitor", "caseworker-probate-authoriser", "caseworker-probate-systemupdate", "caseworker-probate-caseofficer", "caseworker-probate-caseadmin", "caseworker-probate-registrar", "caseworker-probate-superuser", "caseworker-probate-charity", "caseworker-probate-scheduler", "payment", "caseworker-caa"]'

# Assign roles to the xui_webapp client
curl -XPUT \
  ${IDAM_URI}/services/xui_webapp/roles \
 -H "Authorization: AdminApiAuthToken ${authToken}" \
 -H "Content-Type: application/json" \
 -d '["ccd-import", "caseworker", "caseworker-probate", "caseworker-probate-solicitor", "caseworker-probate-superuser", "pui-case-manager", "pui-user-manager", "caseworker-caa"]'

(${binFolder}/idam-create-caseworker.sh caseworker,caseworker-caa,pui-case-manager,pui-user-manager caa-caseworker@mailnesia.com "Password12" "caa" "caseworker")

(${binFolder}/idam-create-service.sh "aac_manage_case_assignment" "aac_manage_case_assignment" "AAAAAAAAAAAAAAAA" "https://manage-case-assignment/oauth2redirect" "false" "profile openid roles manage-user")
(${binFolder}/idam-create-service.sh "xui_mo_webapp" "xui_mo_webapp" "AAAAAAAAAAAAAAAA" "http://localhost:3001/oauth2/callback" "false" "profile openid roles manage-user create-user manage-roles")

(${binFolder}/idam-create-caseworker.sh caseworker,caseworker-probate,caseworker-probate-solicitor,pui-case-manager,pui-user-manager,pui-organisation-manager,pui-caa probatesolicitortestorgman3@gmail.com Probate123 TestOrg3 PBA)

(${binFolder}/wiremock.sh)

