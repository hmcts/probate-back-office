#!/usr/bin/env bash

set -eu

dir=$(dirname ${0})

USER_TOKEN=$(${dir}/idam-lease-user-token.sh ${CCD_CONFIGURER_IMPORTER_USERNAME} ${CCD_CONFIGURER_IMPORTER_PASSWORD})
export USER_TOKEN
SERVICE_TOKEN=$(${dir}/idam-lease-service-token.sh ccd_gw $(docker run --rm hmctsprod.azurecr.io/imported/toolbelt/oathtool --totp -b ${API_GATEWAY_S2S_KEY:-AAAAAAAAAAAAAAAA}))
export SERVICE_TOKEN

# User used during the CCD import and ccd-role creation
# ${dir}/idam-create-caseworker.sh "ccd-import" "ccd.docker.default@hmcts.net"

${dir}/ccd-add-role.sh "prd-aac-system"

${dir}/ccd-add-role.sh payment
${dir}/ccd-add-role.sh citizen
${dir}/ccd-add-role.sh caseworker
${dir}/ccd-add-role.sh caseworker-probate
${dir}/ccd-add-role.sh caseworker-probate-issuer
${dir}/ccd-add-role.sh caseworker-probate-solicitor
${dir}/ccd-add-role.sh caseworker-probate-systemupdate
${dir}/ccd-add-role.sh caseworker-probate-pcqextractor
${dir}/ccd-add-role.sh caseworker-probate-caseofficer
${dir}/ccd-add-role.sh caseworker-probate-rparobot
${dir}/ccd-add-role.sh caseworker-probate-legacysearch
${dir}/ccd-add-role.sh caseworker-probate-caseadmin
${dir}/ccd-add-role.sh caseworker-probate-registrar
${dir}/ccd-add-role.sh caseworker-probate-superuser
${dir}/ccd-add-role.sh caseworker-probate-scheduler
${dir}/ccd-add-role.sh caseworker-probate-charity
${dir}/ccd-add-role.sh caseworker-probate-bulkscan
${dir}/ccd-add-role.sh caseworker-caa
${dir}/ccd-add-role.sh caseworker-approver
${dir}/ccd-add-role.sh TTL_profile
