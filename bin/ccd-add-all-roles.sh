#!/bin/bash
binFolder=$(dirname "$0")
(${binFolder}/idam-create-caseworker.sh caseworker-probate,caseworker-probate-solicitor ProbateSolicitor1@gmail.com)
(${binFolder}/idam-create-caseworker.sh caseworker-probate,caseworker-probate-solicitor ProbateSolicitor2@gmail.com)
(${binFolder}/idam-create-caseworker.sh caseworker-probate,caseworker-probate-issuer ProbateSolCW1@gmail.com)
(${binFolder}/idam-create-caseworker.sh caseworker-probate,caseworker-probate-issuer ProbateSolCW2@gmail.com)
(${binFolder}/idam-create-caseworker.sh caseworker-probate,caseworker-probate-issuer bulkscan+ccd@gmail.com)

(${binFolder}/ccd-add-role.sh caseworker-probate)
(${binFolder}/ccd-add-role.sh caseworker-probate-issuer)
(${binFolder}/ccd-add-role.sh caseworker-probate-examiner)
(${binFolder}/ccd-add-role.sh caseworker-probate-authoriser)
(${binFolder}/ccd-add-role.sh caseworker-probate-solicitor)
(${binFolder}/ccd-add-role.sh citizen)
