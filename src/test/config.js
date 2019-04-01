module.exports = {
    TestFrontendUrl: process.env.TEST_E2E_URL || 'https://www-ccd.aat.platform.hmcts.net',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || true,
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 3,
    TestPathToRun: './paths/grantOfProbatePath.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './output',
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    TestTimeToWaitForText: 60,
    TestEnvUser: process.env.CW_USER_EMAIL || 'probatebackoffice@gmail.com',
    TestEnvPassword: process.env.CW_USER_PASSWORD || 'Monday01'
};
