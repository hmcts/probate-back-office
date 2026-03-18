#!/usr/bin/env bash

set -eu

dir=$(dirname ${0})

${dir}/ccd-add-role.sh caseworker-wa-task-configuration
${dir}/ccd-add-role.sh caseworker-privatelaw-cafcass
${dir}/ccd-add-role.sh caseworker-privatelaw-bulkscan
${dir}/ccd-add-role.sh payments
${dir}/ccd-add-role.sh hearing-centre-team-leader
${dir}/ccd-add-role.sh caseworker-ras-validation
${dir}/ccd-add-role.sh caseworker-privatelaw-readonly
${dir}/ccd-add-role.sh judge
${dir}/ccd-add-role.sh caseworker-privatelaw-superuser
${dir}/ccd-add-role.sh caseworker-privatelaw-externaluser-viewonly
${dir}/ccd-add-role.sh caseworker-privatelaw-courtadmin
${dir}/ccd-add-role.sh tribunal-caseworker
${dir}/ccd-add-role.sh caseworker-privatelaw-systemupdate
${dir}/ccd-add-role.sh ctsc-team-leader
${dir}/ccd-add-role.sh caseworker-privatelaw-solicitor
${dir}/ccd-add-role.sh caseworker-privatelaw-bulkscansystemupdate
${dir}/ccd-add-role.sh caseworker-privatelaw-courtadmin-casecreator
${dir}/ccd-add-role.sh ctsc
${dir}/ccd-add-role.sh courtnav
${dir}/ccd-add-role.sh allocated-magistrate
${dir}/ccd-add-role.sh pui-case-manager
${dir}/ccd-add-role.sh hearing-centre-admin
${dir}/ccd-add-role.sh caseworker-privatelaw-judge
${dir}/ccd-add-role.sh senior-tribunal-caseworker
${dir}/ccd-add-role.sh caseworker-privatelaw-la
