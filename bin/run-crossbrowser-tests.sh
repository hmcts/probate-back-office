#!/bin/bash
set -ex

export TEST_E2E_URL=${TEST_E2E_URL}
export E2E_TEST_PATH="./paths/solicitor/**/*.js"
export CW_USER_EMAIL=${CW_USER_EMAIL}
export CW_USER_PASSWORD=${CW_USER_PASSWORD}
export SOL_USER_EMAIL=${SOL_USER_EMAIL}
export SOL_USER_PASSWORD=${SOL_USER_PASSWORD}
export TESTS_FOR_CROSS_BROWSER='true'
export RETRY_SCENARIOS=0
export SMART_WAIT=60000
export WAIT_FOR_TIMEOUT=60000
export E2E_OUTPUT_DIR='./functional-output/xui/crossbrowser'

if [[ "$BROWSER_GROUP" == "" ]]
then
    EXIT_STATUS=0
    BROWSER_GROUP=chrome yarn test-crossbrowser-e2e || EXIT_STATUS=$?
    BROWSER_GROUP=firefox yarn test-crossbrowser-e2e || EXIT_STATUS=$?
    BROWSER_GROUP=safari yarn test-crossbrowser-e2e || EXIT_STATUS=$?
    BROWSER_GROUP=microsoft yarn test-crossbrowser-e2e || EXIT_STATUS=$?
    echo EXIT_STATUS: $EXIT_STATUS
    exit $EXIT_STATUS
else
    # Compatible with Jenkins parallel crossbrowser pipeline
    yarn test-crossbrowser-e2e
fi
