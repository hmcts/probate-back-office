module.exports = {
    // use for when we know we want to use xui, e.g. when swapping between xui and ccd
    TestBackOfficeUrl: process.env.TEST_XUI_E2E_URL || 'http://localhost:3455',
    TestCcdUrl: process.env.TEST_CCD_E2E_URL || 'http://localhost:3451',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || false,
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 0,
    TestPathToRun: process.env.E2E_TEST_PATH || './paths/**/*.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './functional-output',
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    TestTimeToWaitForText: parseInt(process.env.BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT || 120),
    TestAutoDelayEnabled: process.env.E2E_AUTO_DELAY_ENABLED === 'true',
    TestEnvCwUser: process.env.TEST_USER_EMAIL || 'ProbateSolCW1@gmail.com',
    TestEnvCwPassword: process.env.TEST_USER_PASSWORD || 'Pa55word11',
    TestEnvProfUser: process.env.SOL_USER_EMAIL || 'ProbateSolicitor1@gmail.com',
    TestEnvProfPassword: process.env.SOL_USER_PASSWORD || 'Pa55word11',
    TestForAccessibility: process.env.TESTS_FOR_ACCESSIBILITY === 'true',
    TestForCrossBrowser: process.env.TESTS_FOR_CROSS_BROWSER === 'true',
    // only used when running locally, not in pipeline (where autodelay is on) - other than case matching
    ManualDelayShort: 0.25,
    ManualDelayMedium: 0.5,
    ManualDelayLong: 0.75,
    // if auto delay enabled, is running in pipeline, not locally from individual test path run via package.json script
    SignOutDelayDefault: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    SignInDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    RejectCookies: true,
    CookieRejectDelayDefault: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    CaseDetailsDelayDefault: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0
};
