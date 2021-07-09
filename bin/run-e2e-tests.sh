#!/bin/bash
set -ex

export TEST_XUI_E2E_URL=${TEST_XUI_E2E_URL}
export TEST_CCD_E2E_URL=${TEST_CCD_E2E_URL}
export E2E_TEST_PATH="./paths/solicitor/**/*.js"
# export CW_USER_EMAIL=${CW_USER_EMAIL}
# export CW_USER_PASSWORD=${CW_USER_PASSWORD}
# export SOL_USER_EMAIL=${SOL_USER_EMAIL}
# export SOL_USER_PASSWORD=${SOL_USER_PASSWORD}

# to be returned to picking up from vault - see Sanjay/Mitesh

export CW_USER_EMAIL="probatebackoffice@gmail.com"
export CW_USER_PASSWORD="Monday01"
export SOL_USER_EMAIL="probatesolicitortestorgtest1@gmail.com"
export SOL_USER_PASSWORD="Probate123"

export E2E_AUTO_DELAY_ENABLED='true'
export TESTS_FOR_XUI_SERVICE='true'
export TESTS_FOR_ACCESSIBILITY='true'
export E2E_OUTPUT_DIR='./functional-output/xui'

yarn test:functional

export TEST_XUI_E2E_URL=${TEST_XUI_E2E_URL}
export TEST_CCD_E2E_URL=${TEST_CCD_E2E_URL}
export E2E_TEST_PATH="./paths/caseworker/**/*.js"
# export TEST_USER_EMAIL=${CW_USER_EMAIL}
# export TEST_USER_PASSWORD=${CW_USER_PASSWORD}
# export SOL_USER_EMAIL=${SOL_USER_EMAIL}
# export SOL_USER_PASSWORD=${SOL_USER_PASSWORD}
# to be returned to picking up from vault - see Sanjay/Mitesh

export TEST_USER_EMAIL="probatebackoffice@gmail.com"
export TEST_USER_PASSWORD="Monday01"
export SOL_USER_EMAIL="probatesolicitortestorgtest1@gmail.com"
export SOL_USER_PASSWORD="Probate123"

export TESTS_FOR_XUI_SERVICE='false'
export TESTS_FOR_ACCESSIBILITY='false'
export E2E_OUTPUT_DIR='./functional-output/ccd'

yarn test:functional

export TEST_XUI_E2E_URL=${TEST_XUI_E2E_URL}
export TEST_CCD_E2E_URL=${TEST_CCD_E2E_URL}
export E2E_TEST_PATH="./paths/caseprogress/**/*.js"
# export TEST_USER_EMAIL=${CW_USER_EMAIL}
# export TEST_USER_PASSWORD=${CW_USER_PASSWORD}
# export SOL_USER_EMAIL=${SOL_USER_EMAIL}
# export SOL_USER_PASSWORD=${SOL_USER_PASSWORD}
# to be returned to picking up from vault - see Sanjay/Mitesh

export TEST_USER_EMAIL="probatebackoffice@gmail.com"
export TEST_USER_PASSWORD="Monday01"
export SOL_USER_EMAIL="probatesolicitortestorgtest1@gmail.com"
export SOL_USER_PASSWORD="Probate123"

export TESTS_FOR_XUI_SERVICE='false'
export TESTS_FOR_ACCESSIBILITY='false'
export E2E_OUTPUT_DIR='./functional-output/ccd'

yarn test:functional
