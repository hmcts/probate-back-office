module.exports = {
    // use for when we know we want to use xui, e.g. when swapping between xui and ccd
    TestBackOfficeUrl: process.env.TEST_E2E_URL || 'http://localhost:3455',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || true,
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 0,
    TestPathToRun: process.env.E2E_TEST_PATH || './paths/**/*.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './functional-output',
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    WaitForTextTimeout: parseInt(process.env.BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT || 120),
    TestAutoDelayEnabled: process.env.E2E_AUTO_DELAY_ENABLED === 'true',
    TestEnvCwUser: process.env.TEST_USER_EMAIL || 'ProbateSolCW1@gmail.com',
    TestEnvCwPassword: process.env.TEST_USER_PASSWORD || 'Pa55word11',
    TestEnvProfUser: process.env.SOL_USER_EMAIL || 'ProbateSolicitor1@gmail.com',
    TestEnvProfPassword: process.env.SOL_USER_PASSWORD || 'Pa55word11',
    TestForAccessibility: process.env.TESTS_FOR_ACCESSIBILITY === 'true',
    // only used when running locally, not in pipeline (where autodelay is on) - other than case matching
    ManualDelayShort: 0.25,
    ManualDelayMedium: 0.5,
    ManualDelayLong: 0.75,
    // if auto delay enabled, is running in pipeline, not locally from individual test path run via package.json script
    SignOutDelayDefault: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    SignInDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    SignInDelayDefault: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    RejectCookies: false,
    RejectCookieDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0.5,
    CaseDetailsDelayDefault: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0,
    // Running in the pipeline means a much slower response bringing back existing cases.
    // This is a blunt approach, would be be better if we used an implicit wait
    // and check for events appearing, detect if a callback in progress, and wait for buttons.
    // Perhaps even poll, or use a configured timeout and catch exception (assume no existing cases
    // if timeout exception raised)
    // This was set to 60 for pipeline which seems overkill, perhaps
    // we had a problem one time with ES? Now set back to 6
    CaseMatchesInitialDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 6 : 0.5,
    CaseMatchesLocateRemoveButtonDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 0.5 : 0.25,
    CaseMatchesCompletionDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 0.5 : 0,
    CaseworkerGoButtonClickDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0,
    CaseProgressTabCheckDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    CaseProgressClickSelectFillDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 3 : 0,
    CaseProgressClickGoButtonInitialDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 3 : 0,
    CaseProgressContinueWithoutChangingDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    CaseworkerCaseNavigateDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    CaseProgressSolicitorDetailsDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    CaseProgressSubmitConfirmationDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    CreateCaseDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    DocumentUploadDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0.25,
    EventSummaryDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    GetCaseRefFromUrlDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 4 : 0,
    CaseProgressSignInDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    CreateCaseContinueDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    WillLodgementDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    FindCasesDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0.5
};
