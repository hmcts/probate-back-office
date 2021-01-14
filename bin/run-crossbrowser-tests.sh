#!/bin/bash
set -ex


export TEST_E2E_URL=${TEST_XUI_E2E_URL}
export E2E_TEST_PATH="./paths/solicitor/applyGrantOfProbateMultiExecutor.js"
export TEST_USER_EMAIL=${SOL_USER_EMAIL}
export TEST_USER_PASSWORD=${SOL_USER_PASSWORD}
export TESTS_FOR_XUI_SERVICE='true'

if [[ "$BROWSER_GROUP" == "" ]]
then
    EXIT_STATUS=0
    BROWSER_GROUP=chrome yarn test-crossbrowser-e2e || EXIT_STATUS=$?
    echo EXIT_STATUS: $EXIT_STATUS
    exit $EXIT_STATUS
else
    # Compatible with Jenkins parallel crossbrowser pipeline
    yarn test-crossbrowser-e2e
fi
