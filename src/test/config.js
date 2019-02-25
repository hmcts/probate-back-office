module.exports = {
    TestE2EFrontendUrl: process.env.TEST_E2E_URL || 'http://localhost:9000',
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 3,
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    TestTimeToWaitForText: 60,
    TestEnvUser: process.env.BO_TEST_EMAIL_ADDRESS || 'dummy',
    TestEnvPassword: process.env.BO_TEST_PASSWORD || 'dummy'
};
