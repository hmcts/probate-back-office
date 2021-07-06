export TEST_XUI_E2E_URL=${TEST_XUI_E2E_URL}
export TEST_CCD_E2E_URL=${TEST_CCD_E2E_URL}
export E2E_TEST_PATH="./paths/caseworker/**/*.js"
export TEST_USER_EMAIL=${CW_USER_EMAIL}
export TEST_USER_PASSWORD=${CW_USER_PASSWORD}
export TESTS_FOR_XUI_SERVICE='false'

yarn test:functional