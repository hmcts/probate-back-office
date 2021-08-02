#!/bin/bash
binFolder=$(dirname "$0")

(${binFolder}/ccd-add-role.sh caseworker-caa)
(${binFolder}/xui-add-role.sh caseworker-caa)

(${binFolder}/idam-create-caseworker.sh caseworker,caseworker-caa,pui-case-manager,pui-user-manager caa-caseworker@mailnesia.com "Password12" "caa" "caseworker")

(${binFolder}/idam-create-service.sh "aac_manage_case_assignment" "aac_manage_case_assignment" "AAAAAAAAAAAAAAAA" "https://manage-case-assignment/oauth2redirect" "false" "profile openid roles manage-user"
