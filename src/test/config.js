module.exports = {
    TestBackOfficeUrl: process.env.TEST_E2E_URL || 'http://localhost:3455',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || false,
    TestRetryFeatures: 0, // process.env.RETRY_FEATURES || 2,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 2,
    TestPathToRun: process.env.E2E_TEST_PATH || './paths/**/*.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './functional-output',
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    WaitForTextTimeout: parseInt(process.env.BO_E2E_TEST_TIME_TO_WAIT_FOR_TEXT || 200),
    TestAutoDelayEnabled: process.env.E2E_AUTO_DELAY_ENABLED === 'true',
    TestEnvCwUser: process.env.CW_USER_EMAIL || 'ProbateSolCW1@gmail.com',
    TestEnvCwPassword: process.env.CW_USER_PASSWORD || 'Pa55word11',
    TestEnvSuperCwUser: process.env.CW_SUPERUSER_EMAIL || 'probatesuperuser2@gmail.com',
    TestEnvSuperCwPassword: process.env.CW_SUPERUSER_PASSWORD || 'Monday01',
    TestEnvProfUser: process.env.SOL_USER_EMAIL || 'probatesolicitortestorgtest2@gmail.com',
    TestEnvProfPassword: process.env.SOL_USER_PASSWORD || 'Pa55wordTest',
    //  TestEnvProfUser2 User for share case e2e only
    //  For Local Environment please use below credential for User2 to run shareCase, you should be able to share that case with the other PP user
    //  probatesolicitortestorg2test1@gmail.com   Pass- Probate123
    TestEnvProfUserSAC: 'probate.practitioner.aat.test@gmail.com',
    TestEnvProfPasswordSAC: 'Probate123',
    TestEnvProfUserNoc: 'probate.pp1.org2@gmail.com',
    TestEnvProfPasswordNoc: 'Probate123',
    TestForAccessibility: process.env.TESTS_FOR_ACCESSIBILITY === 'true',
    //TestForCrossBrowser: process.env.TESTS_FOR_CROSS_BROWSER === 'true'
    // only used when running locally, not in pipeline (where autodelay is on) - other than case matching
    ManualDelayShort: 0.25,
    ManualDelayMedium: 0.5,
    ManualDelayLong: 0.75,
    // if auto delay enabled, is running in pipeline, not locally from individual test path run via package.json script
    SignOutDelayDefault: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    SignInDelayDefault: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    RejectCookies: false,
    RejectCookieDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    CaseDetailsDelayDefault: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0,
    MultiUserSignInDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    // Running in the pipeline means a much slower response bringing back existing cases.
    // This is a blunt approach, would be be better if we used an implicit wait
    // and check for events appearing, detect if a callback in progress, and wait for buttons.
    // Perhaps even poll, or use a configured timeout and catch exception (assume no existing cases
    // if timeout exception raised)
    // This was set to 60 for pipeline which seems overkill, perhaps
    // we had a problem one time with ES? Now set back to 6
    CaseMatchesInitialDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0.25,
    CaseMatchesLocateRemoveButtonDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 0.5 : 0.25,
    CaseMatchesAddNewButtonClickDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 0.5 : 0,
    CaseMatchesCompletionDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 0.5 : 0,
    CaseworkerGoButtonClickDelay: 3, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0,
    CaseProgressTabCheckDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    CaseProgressClickSelectFillDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 3 : 0,
    CaseProgressClickGoButtonInitialDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 3 : 0,
    CaseProgressContinueWithoutChangingDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    CaseworkerCaseNavigateDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    CaseProgressSolicitorDetailsDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    CaseProgressSubmitConfirmationDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    CreateCaseDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 0,
    DocumentUploadDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0.5,
    EventSummaryDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    GetCaseRefFromUrlDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 4 : 0,
    CaseProgressSignInDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0,
    CreateCaseContinueDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    WillLodgementDelay: 0, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 5 : 0,
    FindCasesInitialDelay: 1, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 1 : 0.75,
    FindCasesDelay: process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 2 : 1,
    CheckYourAnswersDelay: 0.5, // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 0.5 : 0.5
    ExecutorsPageDelay: 0 // process.env.E2E_AUTO_DELAY_ENABLED === 'true' ? 10 : 0
};
