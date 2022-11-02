#!/bin/bash
set -ex

export TEST_E2E_URL=${TEST_E2E_URL}
export E2E_TEST_PATH="./paths/solicitor/**/*.js"
export SOL_USER_EMAIL=${SOL_USER_EMAIL}
export SOL_USER_PASSWORD=${SOL_USER_PASSWORD}
export SOL2_USER_EMAIL=${SOL2_USER_EMAIL}
export SOL2_USER_PASSWORD=${SOL2_USER_PASSWORD}
export E2E_AUTO_DELAY_ENABLED='true'
export TESTS_FOR_ACCESSIBILITY='false'
export E2E_OUTPUT_DIR='./functional-output/solicitor/xui'
export RETRY_SCENARIOS=2
export BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT='200'

yarn test:functional

################

export TEST_E2E_URL=${TEST_E2E_URL}
export E2E_TEST_PATH="./paths/caseworker/**/*.js"
export CW_USER_EMAIL=${CW_USER_EMAIL}
export CW_USER_PASSWORD=${CW_USER_PASSWORD}
export E2E_AUTO_DELAY_ENABLED='true'
export TESTS_FOR_ACCESSIBILITY='false'
export E2E_OUTPUT_DIR='./functional-output/caseworker/xui'
export RETRY_SCENARIOS=2
export BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT='200'

yarn test:functional

################

export TEST_E2E_URL=${TEST_E2E_URL}
export E2E_TEST_PATH="./paths/caseprogress/**/*.js"
export CW_USER_EMAIL=${CW_USER_EMAIL}
export CW_USER_PASSWORD=${CW_USER_PASSWORD}
export SOL_USER_EMAIL=${SOL_USER_EMAIL}
export SOL_USER_PASSWORD=${SOL_USER_PASSWORD}
export TESTS_FOR_ACCESSIBILITY='false'
export E2E_OUTPUT_DIR='./functional-output/caseprogress/xui'
export RETRY_SCENARIOS=2
export BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT='200'

yarn test:functional

################

export TEST_E2E_URL=${TEST_E2E_URL}
export E2E_TEST_PATH="./paths/multiuser/**/*.js"
export CW_USER_EMAIL=${CW_USER_EMAIL}
export CW_USER_PASSWORD=${CW_USER_PASSWORD}
export SOL_USER_EMAIL=${SOL_USER_EMAIL}
export SOL_USER_PASSWORD=${SOL_USER_PASSWORD}
export E2E_AUTO_DELAY_ENABLED='true'
export TESTS_FOR_ACCESSIBILITY='false'
export E2E_OUTPUT_DIR='./functional-output/multiuser/xui'
export RETRY_SCENARIOS=2
export BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT='200'

# yarn test:functional
