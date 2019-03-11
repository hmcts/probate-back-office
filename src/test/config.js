module.exports = {
    TestE2EFrontendUrl: process.env.TEST_E2E_URL || 'localhost:3000',
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 3,
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    TestTimeToWaitForText: 60,
    TestEnvUser: process.env.CW_USER_EMAIL || 'dummy',
    TestEnvPassword: process.env.CW_USER_PASSWORD || 'dummy'
};
