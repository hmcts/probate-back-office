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
${dir}/idam-role.sh caseworker-probate-rparobot
${dir}/idam-role.sh caseworker-probate-caseadmin
${dir}/idam-role.sh caseworker-probate-registrar
${dir}/idam-role.sh caseworker-probate-superuser
${dir}/idam-role.sh caseworker-probate-bulkscan
${dir}/idam-role.sh caseworker-probate-scheduler
${dir}/idam-role.sh caseworker-probate-charity
${dir}/idam-role.sh payment
${dir}/idam-role.sh ccd-import
${dir}/idam-role.sh caseworker-caa
${dir}/idam-role.sh caseworker-approver

# User used during the CCD import and ccd-role creation
${dir}/idam-create-caseworker.sh "ccd-import" "ccd.docker.default@hmcts.net"

${dir}/idam-role.sh "prd-aac-system"
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
${dir}/ccd-add-role.sh caseworker-probate-caseadmin
${dir}/ccd-add-role.sh caseworker-probate-registrar
${dir}/ccd-add-role.sh caseworker-probate-superuser
${dir}/ccd-add-role.sh caseworker-probate-scheduler
${dir}/ccd-add-role.sh caseworker-probate-charity
${dir}/ccd-add-role.sh caseworker-probate-bulkscan
${dir}/ccd-add-role.sh caseworker-caa
${dir}/ccd-add-role.sh caseworker-approver
