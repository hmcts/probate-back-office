#!/bin/bash
binFolder=$(dirname "$0")

(${binFolder}/idam-create-caseworker.sh payment,caseworker-probate,caseworker-probate-issuer ProbateSolCW1@gmail.com)
(${binFolder}/idam-create-caseworker.sh payment,caseworker-probate,caseworker-probate-issuer ProbateSolCW2@gmail.com)
(${binFolder}/idam-create-caseworker.sh caseworker-probate,caseworker-probate-solicitor ProbateSolicitor1@gmail.com)
(${binFolder}/idam-create-caseworker.sh caseworker-probate,caseworker-probate-solicitor ProbateSolicitor2@gmail.com)
(${binFolder}/idam-create-caseworker.sh caseworker-probate,caseworker-probate-systemupdate bulkscan+ccd@gmail.com)

(${binFolder}/idam-create-caseworker.sh payment,caseworker-probate,caseworker-probate-caseofficer ProbateCaseOfficer@gmail.com)
(${binFolder}/idam-create-caseworker.sh payment,caseworker-probate,caseworker-probate-caseadmin ProbateCaseAdmin@gmail.com)
(${binFolder}/idam-create-caseworker.sh payment,caseworker-probate,caseworker-probate-registrar ProbateRegistrar@gmail.com)
(${binFolder}/idam-create-caseworker.sh payment,caseworker-probate,caseworker-probate-superuser ProbateSuperuser@gmail.com)

(${binFolder}/ccd-add-role.sh payment)
(${binFolder}/ccd-add-role.sh citizen)
(${binFolder}/ccd-add-role.sh caseworker-probate)
(${binFolder}/ccd-add-role.sh caseworker-probate-issuer)
(${binFolder}/ccd-add-role.sh caseworker-probate-solicitor)
(${binFolder}/ccd-add-role.sh caseworker-probate-authoriser)
(${binFolder}/ccd-add-role.sh caseworker-probate-systemupdate)

(${binFolder}/ccd-add-role.sh caseworker-probate-caseofficer)
(${binFolder}/ccd-add-role.sh caseworker-probate-caseadmin)
(${binFolder}/ccd-add-role.sh caseworker-probate-registrar)
(${binFolder}/ccd-add-role.sh caseworker-probate-superuser)
