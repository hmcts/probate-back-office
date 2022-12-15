#!/usr/bin/env bash

set -eu

dir=$(dirname ${0})



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
${dir}/ccd-add-role.sh caseworker-probate-caseadmin
${dir}/ccd-add-role.sh caseworker-probate-registrar
${dir}/ccd-add-role.sh caseworker-probate-superuser
${dir}/ccd-add-role.sh caseworker-probate-scheduler
${dir}/ccd-add-role.sh caseworker-probate-charity
${dir}/ccd-add-role.sh caseworker-probate-bulkscan
${dir}/ccd-add-role.sh caseworker-caa
