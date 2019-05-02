module.exports = {
    TestFrontendUrl: process.env.TEST_E2E_URL || 'localhost:3000',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || true,
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 3,
    TestPathToRun: './paths/**/*.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './output',
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    TestTimeToWaitForText: 60,
    TestEnvUser: process.env.CW_USER_EMAIL || 'dummy',
    TestEnvPassword: process.env.CW_USER_PASSWORD || 'dummy'
};
