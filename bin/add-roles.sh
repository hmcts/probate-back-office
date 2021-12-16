#!/usr/bin/env bash

set -eu

dir=$(dirname ${0})

${dir}/idam-role.sh caseworker
${dir}/idam-role.sh caseworker-probate
${dir}/idam-role.sh caseworker-probate-issuer
${dir}/idam-role.sh caseworker-probate-solicitor
${dir}/idam-role.sh caseworker-probate-authoriser
${dir}/idam-role.sh caseworker-probate-systemupdate
${dir}/idam-role.sh caseworker-probate-caseofficer
${dir}/idam-role.sh caseworker-probate-caseadmin
${dir}/idam-role.sh caseworker-probate-registrar
${dir}/idam-role.sh caseworker-probate-superuser
${dir}/idam-role.sh caseworker-probate-bulkscan
${dir}/idam-role.sh caseworker-probate-scheduler
${dir}/idam-role.sh caseworker-probate-charity
${dir}/idam-role.sh payment
${dir}/idam-role-assignable.sh ccd-import

# User used during the CCD import and ccd-role creation
${dir}/idam-create-caseworker.sh "ccd-import" "ccd.docker.default@hmcts.net"

${dir}/idam-role.sh "prd-aac-system"
${dir}/ccd-add-role.sh "prd-aac-system"

${dir}/ccd-add-role.sh "citizen"
