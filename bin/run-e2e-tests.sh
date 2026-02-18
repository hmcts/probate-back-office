#!/bin/bash
set -euo pipefail
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

# Clean up old reports
rm -rf ./functional-output/reports
mkdir -p ./functional-output/reports

# Run with xvfb
export DISPLAY=:99
Xvfb :99 -screen 0 1280x720x24 &
XVFB_PID=$!

# Always clean up Xvfb
trap 'set +e; [[ -n "${XVFB_PID:-}" ]] && kill "$XVFB_PID" >/dev/null 2>&1' EXIT

# Run tests but don't abort the script; capture exit code
set +e
yarn test:functional-chromium
TEST_STATUS=$?
set -e

echo "Full e2e tests completed with status: $TEST_STATUS"

# Exit with the tests' status (fails pipeline if tests failed)
exit $TEST_STATUS
