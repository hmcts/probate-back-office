'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');

const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const createWillLodgementConfig = require('src/test/end-to-end/pages/createWillLodgement/createWillLodgementConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');
const documentUploadSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/documentUploadSummaryConfig');
const commentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/commentSummaryConfig');
const generateDepositReceiptSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/generateDepositReceiptSummaryConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caseMatchesConfig');
const caseMatchesCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/caseMatchesCommentSummaryConfig');
const withdrawalSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/withdrawalSummaryConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/historyTabConfig');
const generalDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/generalDetailsTabConfig');
const testatorTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/testatorTabConfig');
const executorTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/executorTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/documentUploadTabConfig');
const generalDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/generalDetailsUpdateTabConfig');
const testatorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/testatorUpdateTabConfig');
const executorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/executorUpdateTabConfig');
const generateDepositReceiptTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/generateDepositReceiptTabConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/caseMatchesTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Will Lodgement for a Personal Applicant', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text, createCaseConfig.list3_text);
    I.enterWillLodgementPage1('create');
    I.enterWillLodgementPage2('create');
    I.enterWillLodgementPage3('create');
    I.checkMyAnswers();

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop().match(/.{4}/g).join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig);
    I.seeCaseDetails(caseRef, generalDetailsTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, testatorTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, executorTabConfig, createWillLodgementConfig);

    I.chooseNextStep('Upload document');
    I.uploadDocument(caseRef);
    I.enterEventSummary(caseRef, documentUploadSummaryConfig);
    I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    I.chooseNextStep('Amend will lodgement');
    I.enterWillLodgementPage1('update');
    I.enterWillLodgementPage2('update');
    I.enterWillLodgementPage3('update');
    I.checkMyAnswers();
    I.seeCaseDetails(caseRef, generalDetailsUpdateTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, testatorUpdateTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, executorUpdateTabConfig, createWillLodgementConfig);

    I.chooseNextStep('Add comment');
    I.enterComment(caseRef, commentSummaryConfig);
    I.seeCaseDetails(caseRef, historyTabConfig, commentSummaryConfig);

    I.chooseNextStep('Generate deposit receipt');
    I.generateDepositReceipt(caseRef, generateDepositReceiptSummaryConfig);
    I.seeCaseDetails(caseRef, generateDepositReceiptTabConfig, generateDepositReceiptSummaryConfig);

    I.chooseNextStep('Match application');
    I.selectCaseMatches(caseRef, caseMatchesConfig);
    I.enterCaseMatchesComment(caseRef, caseMatchesCommentSummaryConfig);
    I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    I.chooseNextStep('Withdraw will');
    I.selectWithdrawalReason(caseRef);
    I.enterWithdrawalSummary(caseRef, withdrawalSummaryConfig);
    I.seeCaseDetails(caseRef, historyTabConfig, withdrawalSummaryConfig);

}).retry(testConfig.TestRetryScenarios);
