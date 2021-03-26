#!/bin/bash
set -ex

export TEST_E2E_URL=${TEST_XUI_E2E_URL}
export E2E_TEST_PATH="./paths/solicitor/**/*.js"
export TEST_USER_EMAIL=${SOL_USER_EMAIL}
export TEST_USER_PASSWORD=${SOL_USER_PASSWORD}
export PROF_USER_EMAIL=${SOL_USER_EMAIL}
export PROF_USER_PASSWORD=${SOL_USER_PASSWORD}
export E2E_AUTO_DELAY_ENABLED='true'
export TESTS_FOR_XUI_SERVICE='true'
export TESTS_FOR_ACCESSIBILITY='true'
export E2E_OUTPUT_DIR='./functional-output/xui'


export TEST_E2E_URL=${TEST_CCD_E2E_URL}
export E2E_TEST_PATH="./paths/caseworker/**/caseProgressStandardPath.js"
export TEST_USER_EMAIL="probatebackoffice@gmail.com"
export TEST_USER_PASSWORD="Monday01"


export PROF_USER_EMAIL="probatesolicitorpreprod@gmail.com"
export PROF_USER_PASSWORD="Monday01"
echo  PROF_USER_EMAIL
echo  PROF_USER_PASSWORD
export TESTS_FOR_XUI_SERVICE='false'
export TESTS_FOR_ACCESSIBILITY='false'
export E2E_OUTPUT_DIR='./functional-output/ccd'

yarn test:functional
