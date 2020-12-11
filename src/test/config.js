module.exports = {
    TestBackOfficeUrl: process.env.TEST_E2E_URL || 'http://localhost:3451',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || false,
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 2,
    TestPathToRun: process.env.E2E_TEST_PATH || './paths/**/*.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './functional-output',
    TestFrontendUrl: process.env.TEST_E2E_URL || 'http://localhost:3451',
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    TestTimeToWaitForText: parseInt(process.env.BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT || 60),
    TestActionWaitTime: parseInt(process.env.BO_E2E_TEST_ACTION_WAIT_TIME || '1500'),
    TestAutoDelayEnabled: process.env.BO_E2E_AUTO_DELAY_ENABLED === 'true',
    TestEnvUser: process.env.TEST_USER_EMAIL || 'ProbateSolCW1@gmail.com',
    TestEnvPassword: process.env.TEST_USER_PASSWORD || 'Pa55word11',
    TestEnvProfUser: process.env.PROF_USER_EMAIL || 'ProbateSolicitor1@gmail.com',
    TestEnvProfPassword: process.env.CW_USER_PASSWORD || 'Pa55word11',
    TestForXUI: process.env.TESTS_FOR_XUI_SERVICE === 'true'
};
