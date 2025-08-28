#!/bin/bash
set -x

# Base export
export TEST_E2E_URL=${TEST_E2E_URL}
export E2E_AUTO_DELAY_ENABLED='true'
export TESTS_FOR_ACCESSIBILITY='false'
export RETRY_SCENARIOS=2
export BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT='200'

# Users
export SOL_USER_EMAIL=${SOL_USER_EMAIL}
export SOL_USER_PASSWORD=${SOL_USER_PASSWORD}
export SOL2_USER_EMAIL=${SOL2_USER_EMAIL}
export SOL2_USER_PASSWORD=${SOL2_USER_PASSWORD}
export CW_USER_EMAIL=${CW_USER_EMAIL}
export CW_USER_PASSWORD=${CW_USER_PASSWORD}

# Paths
export E2E_OUTPUT_DIR='./functional-output'

# Run with xvfb
export DISPLAY=:99
Xvfb :99 -screen 0 1280x720x24 &
XVFB_PID=$!

yarn test:functional
yarn merge-reports

# Clean up
kill $XVFB_PID
