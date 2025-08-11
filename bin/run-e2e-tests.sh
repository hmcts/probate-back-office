#!/bin/bash
set -ex

# Base export
export TEST_E2E_URL=${TEST_E2E_URL}
export E2E_AUTO_DELAY_ENABLED='true'
export TESTS_FOR_ACCESSIBILITY='false'
export RETRY_SCENARIOS=2
export BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT='200'

# Paths
export E2E_OUTPUT_DIR='./functional-output'

# Run with xvfb
export DISPLAY=:99
Xvfb :99 -screen 0 1280x720x24 &
XVFB_PID=$!

yarn test:functional

# Clean up
kill $XVFB_PID
