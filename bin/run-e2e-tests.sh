#!/bin/bash
set -ex

export TEST_E2E_URL=${TEST_XUI_E2E_URL}
export E2E_TEST_PATH="./paths/solicitor/**/*.js"
export TEST_USER_EMAIL=${SOL_USER_EMAIL}
export TEST_USER_PASSWORD=${SOL_USER_PASSWORD}
export E2E_AUTO_DELAY_ENABLED='true'
export TESTS_FOR_XUI_SERVICE='true'
export TESTS_FOR_ACCESSIBILITY='true'

yarn test:functional

export TEST_E2E_URL=${TEST_CCD_E2E_URL}
export E2E_TEST_PATH="./paths/caseworker/**/*.js"
export TEST_USER_EMAIL=${CW_USER_EMAIL}
export TEST_USER_PASSWORD=${CW_USER_PASSWORD}
export PROF_USER_EMAIL=${SOL_USER_EMAIL}
export PROF_USER_PASSWORD=${SOL_USER_PASSWORD}
export TESTS_FOR_XUI_SERVICE='false'
export TESTS_FOR_ACCESSIBILITY='false'

yarn test:functional


