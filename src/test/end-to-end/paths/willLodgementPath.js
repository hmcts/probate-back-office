'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');

const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const createWillLodgementConfig = require('src/test/end-to-end/pages/createWillLodgement/createWillLodgementConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');
const documentUploadSummaryConfig = require('src/test/end-to-end/pages/eventSummary/documentUploadSummaryConfig');

const addCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/addCommentSummaryConfig');

const generateDepositReceiptConfig = require('src/test/end-to-end/pages/generateDepositReceipt/generateDepositReceiptConfig');
const generateDepositReceiptSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/generateDepositReceiptSummaryConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/willLodgement/caseMatchesConfig');
const caseMatchesCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/caseMatchesCommentSummaryConfig');
const withdrawWillSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/withdrawWillSummaryConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/historyTabConfig');

const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/caseDetailsTabConfig');
const testatorTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/testatorTabConfig');
const executorTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/executorTabConfig');

const caseDetailsTabUpdateConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/caseDetailsTabUpdateConfig');
const testatorTabUpdateConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/testatorTabUpdateConfig');
const executorTabUpdateConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/executorTabUpdateConfig');
const documentsTabUploadDocumentConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/documentsTabUploadDocumentConfig');
const documentsTabGenerateDepositReceiptConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/documentsTabGenerateDepositReceiptConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/caseMatchesTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Will Lodgement Workflow - E2E test 01 - Will Lodgement for a Personal Applicant - Create a will lodgement -> Withdraw will', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    let nextStepName = 'Create a will lodgement';
    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text, createCaseConfig.list3_text);
    I.enterWillLodgementPage1('create');
    I.enterWillLodgementPage2('create');
    I.enterWillLodgementPage3('create');
    I.checkMyAnswers();
    let endState = 'Will lodgement created';

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop().match(/.{4}/g).join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseDetailsTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, testatorTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, executorTabConfig, createWillLodgementConfig);

    nextStepName = 'Upload document';
    I.chooseNextStep(nextStepName);
    I.uploadDocument(caseRef);
    I.enterEventSummary(caseRef, documentUploadSummaryConfig);
    // Note that End State does not change when uploading a document.
    I.seeCaseDetails(caseRef, historyTabConfig, documentUploadSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, documentsTabUploadDocumentConfig, documentUploadConfig);

    nextStepName = 'Add comment';
    I.chooseNextStep(nextStepName);
    I.enterComment(caseRef, addCommentSummaryConfig);
    // Note that End State does not change when adding a comment.
    I.seeCaseDetails(caseRef, historyTabConfig, addCommentSummaryConfig, nextStepName, endState);

    nextStepName = 'Amend will lodgement';
    I.chooseNextStep(nextStepName);
    I.enterWillLodgementPage1('update');
    I.enterWillLodgementPage2('update');
    I.enterWillLodgementPage3('update');
    I.checkMyAnswers();
    // Note that End State does not change when amending a Will Lodgement.
    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseDetailsTabUpdateConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, testatorTabUpdateConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, executorTabUpdateConfig, createWillLodgementConfig);

    nextStepName = 'Generate deposit receipt';
    I.chooseNextStep(nextStepName);
    I.generateDepositReceipt(caseRef, generateDepositReceiptSummaryConfig);
    // Note that End State does not change when generating a deposit receipt.
    I.seeCaseDetails(caseRef, historyTabConfig, generateDepositReceiptSummaryConfig, nextStepName, endState);
    // When generating a deposit receipt, the Date added for the deposit receipt document is set to today
    generateDepositReceiptConfig.dateAdded = dateFns.format(new Date(), 'DD MMM YYYY');
    I.seeCaseDetails(caseRef, documentsTabGenerateDepositReceiptConfig, generateDepositReceiptConfig);

    nextStepName = 'Match application';
    I.chooseNextStep(nextStepName);
    I.selectCaseMatchesForWillLodgement(caseRef, caseMatchesConfig);
    I.enterCaseMatchesComment(caseRef, caseMatchesCommentSummaryConfig);
    endState = 'Will lodged';
    I.seeCaseDetails(caseRef, historyTabConfig, caseMatchesCommentSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Withdraw will';
    I.chooseNextStep(nextStepName);
    I.selectWithdrawalReason(caseRef);
    I.enterWithdrawalSummary(caseRef, withdrawWillSummaryConfig);
    endState = 'Will withdrawn';
    I.seeCaseDetails(caseRef, historyTabConfig, withdrawWillSummaryConfig, nextStepName, endState);

    I.click('#sign-out');

}).retry(testConfig.TestRetryScenarios);
