#!/bin/bash
binFolder=$(dirname "$0")

(${binFolder}/ccd-add-role.sh payment)
(${binFolder}/ccd-add-role.sh citizen)
(${binFolder}/ccd-add-role.sh caseworker)
(${binFolder}/ccd-add-role.sh caseworker-probate)
(${binFolder}/ccd-add-role.sh caseworker-probate-issuer)
(${binFolder}/ccd-add-role.sh caseworker-probate-solicitor)
(${binFolder}/ccd-add-role.sh caseworker-probate-authoriser)
(${binFolder}/ccd-add-role.sh caseworker-probate-systemupdate)
(${binFolder}/ccd-add-role.sh caseworker-probate-pcqextractor)
(${binFolder}/ccd-add-role.sh caseworker-probate-caseofficer)
(${binFolder}/ccd-add-role.sh caseworker-probate-rparobot)
(${binFolder}/ccd-add-role.sh caseworker-probate-caseadmin)
(${binFolder}/ccd-add-role.sh caseworker-probate-registrar)
(${binFolder}/ccd-add-role.sh caseworker-probate-superuser)
(${binFolder}/ccd-add-role.sh caseworker-probate-scheduler)
(${binFolder}/ccd-add-role.sh caseworker-probate-bulkscan)
(${binFolder}/ccd-add-role.sh caseworker-probate-caseadmin)
(${binFolder}/ccd-add-role.sh TTL_profile)

