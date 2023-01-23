#!/bin/bash
set -ex

export TEST_E2E_URL=${TEST_E2E_URL}
export E2E_TEST_PATH="./paths/caseprogress/pr_specific/caseProgressStandardPath.js"
export CW_USER_EMAIL=${CW_USER_EMAIL}
export CW_USER_PASSWORD=${CW_USER_PASSWORD}
export SOL_USER_EMAIL=${SOL_USER_EMAIL}
export SOL_USER_PASSWORD=${SOL_USER_PASSWORD}
export TESTS_FOR_ACCESSIBILITY='false'
export E2E_OUTPUT_DIR='./functional-output/caseprogress/xui'
export RETRY_SCENARIOS=0
export BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT='200'

yarn test:functional
