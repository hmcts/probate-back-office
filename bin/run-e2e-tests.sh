#!/bin/bash
set -ex

export TEST_E2E_URL=${TEST_XUI_E2E_URL}
export E2E_TEST_PATH="./paths/multiuser/*.js"
export TEST_USER_EMAIL="probatecaseworker@gmail.com"
export TEST_USER_PASSWORD="Monday01"
export PROF_USER_EMAIL="probatesolicitortestorgtest1@gmail.com"
export PROF_USER_PASSWORD="Probate123"
export E2E_AUTO_DELAY_ENABLED='true'
export TESTS_FOR_ACCESSIBILITY='false'
export E2E_OUTPUT_DIR='./functional-output/xui'
export BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT=30
export RETRY_SCENARIOS=2
yarn test:functional

export E2E_TEST_PATH="./paths/singleuser/*.js"
yarn test:functional
