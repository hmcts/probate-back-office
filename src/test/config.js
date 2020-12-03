module.exports = {
    TestFrontendUrl: process.env.TEST_E2E_URL || 'http://localhost:3451',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || false,
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 3,
    TestPathToRun: process.env.E2E_TEST_PATH || './paths/**/*.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './functional-output',
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    TestTimeToWaitForText: 60,
    TestEnvUser: process.env.CW_USER_EMAIL || 'dummy',
    TestEnvPassword: process.env.CW_USER_PASSWORD || 'dummy'
};
