'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');

const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const createWillLodgementConfig = require('src/test/end-to-end/pages/createWillLodgement/createWillLodgementConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');
const documentUploadSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/documentUploadSummaryConfig');
const commentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/commentSummaryConfig');
const generateDepositReceiptSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/generateDepositReceiptSummaryConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caseMatchesConfig');
const caseMatchesCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/caseMatchesCommentSummaryConfig');
const withdrawalSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/withdrawalSummaryConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/historyTabConfig');
const generalDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/generalDetailsTabConfig');
const testatorTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/testatorTabConfig');
const executorTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/executorTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/documentUploadTabConfig');
const generalDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/generalDetailsUpdateTabConfig');
const testatorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/testatorUpdateTabConfig');
const executorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/executorUpdateTabConfig');
const generateDepositReceiptTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/generateDepositReceiptTabConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/caseMatchesTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Will Lodgement for a Personal Applicant', async function (I) {

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
    const caseRef = url.split('/').pop()
        .match(/.{4}/g)
        .join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, generalDetailsTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, testatorTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, executorTabConfig, createWillLodgementConfig);

    nextStepName = 'Upload document';
    I.chooseNextStep(nextStepName);
    I.uploadDocument(caseRef);
    I.enterEventSummary(caseRef, documentUploadSummaryConfig);
    // Note that End State does not change when uploading documents.
    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    nextStepName = 'Amend will lodgement';
    I.chooseNextStep(nextStepName);
    I.enterWillLodgementPage1('update');
    I.enterWillLodgementPage2('update');
    I.enterWillLodgementPage3('update');
    I.checkMyAnswers();
    // Note that End State does not change when amending a Will Lodgement.
    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, generalDetailsUpdateTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, testatorUpdateTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, executorUpdateTabConfig, createWillLodgementConfig);

    nextStepName = 'Add comment';
    I.chooseNextStep(nextStepName);
    I.enterComment(caseRef, commentSummaryConfig);
    // Note that End State does not change when adding a comment.
    I.seeCaseDetails(caseRef, historyTabConfig, commentSummaryConfig, nextStepName, endState);

    nextStepName = 'Generate deposit receipt';
    I.chooseNextStep(nextStepName);
    I.generateDepositReceipt(caseRef, generateDepositReceiptSummaryConfig);
    // Note that End State does not change when generating a deposit receipt.
    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, generateDepositReceiptTabConfig, generateDepositReceiptSummaryConfig);

    nextStepName = 'Match application';
    I.chooseNextStep(nextStepName);
    I.selectCaseMatches(caseRef, caseMatchesConfig);
    I.enterCaseMatchesComment(caseRef, caseMatchesCommentSummaryConfig);
    endState = 'Will lodged';
    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Withdraw will';
    I.chooseNextStep(nextStepName);
    I.selectWithdrawalReason(caseRef);
    I.enterWithdrawalSummary(caseRef, withdrawalSummaryConfig);
    endState = 'Will withdrawn';
    I.seeCaseDetails(caseRef, historyTabConfig, withdrawalSummaryConfig, nextStepName, endState);

}).retry(testConfig.TestRetryScenarios);
