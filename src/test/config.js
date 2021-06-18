module.exports = {
    TestBackOfficeUrl: process.env.TEST_E2E_URL || 'https://xui-webapp-pr-944.service.core-compute-preview.internal',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || true,
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 2,
    TestPathToRun: process.env.E2E_TEST_PATH || './paths/**/*.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './functional-output',
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    TestTimeToWaitForText: parseInt(process.env.BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT || 120),
    TestAutoDelayEnabled: process.env.E2E_AUTO_DELAY_ENABLED === 'true',
    TestEnvUser: process.env.TEST_USER_EMAIL || 'probatesolicitortestorgtest1@gmail.com',
    TestEnvPassword: process.env.TEST_USER_PASSWORD || 'Probate123',
    TestEnvProfUser: process.env.PROF_USER_EMAIL || 'probatesolicitortestorgtest1@gmail.com',
    TestEnvProfPassword: process.env.PROF_USER_PASSWORD || 'Probate123',
    TestForXUI: true,
    TestForAccessibility: false,
    TestForCrossBrowser: false
};
