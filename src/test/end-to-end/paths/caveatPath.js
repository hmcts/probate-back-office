'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');

const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const createCaveatConfig = require('src/test/end-to-end/pages/createCaveat/createCaveatConfig');
const eventsConfig = require('src/test/end-to-end/pages/caseDetails/caveat/eventConfig');
//const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');
//const documentUploadSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/documentUploadSummaryConfig');
//const commentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/commentSummaryConfig');
//const generateDepositReceiptSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/generateDepositReceiptSummaryConfig');
//const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caseMatchesConfig');
//const caseMatchesCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/caseMatchesCommentSummaryConfig');
//const withdrawalSummaryConfig = require('src/test/end-to-end/pages/eventSummary/willLodgement/withdrawalSummaryConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/historyTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseDetailsTabConfig');
const deceasedDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/deceasedDetailsTabConfig');
const caveatorDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatorDetailsTabConfig');
const caveatDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatDetailsTabConfig');

const documentsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/documentsTabConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseMatchesTabConfig');

//const generalDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/generalDetailsUpdateTabConfig');
//const testatorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/testatorUpdateTabConfig');
//const executorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/executorUpdateTabConfig');
//const generateDepositReceiptTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/generateDepositReceiptTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Caveat for a Personal Applicant', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    const nextStepName = 'Raise a caveat';
    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_raise_caveat);
    I.enterCaveatPage1('create');
    I.enterCaveatPage2('create');
    I.enterCaveatPage3('create');
    I.checkMyAnswers();
    const endState = 'Caveat raised';

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop().match(/.{4}/g).join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseDetailsTabConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, deceasedDetailsTabConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, caveatorDetailsTabConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, caveatDetailsTabConfig, createCaveatConfig);

    nextStepName = 'Email caveator';
    I.chooseNextStep(nextStepName);
    // Note that End State does not change when emailing the caveator.
    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);

    nextStepName = 'Caveat match';
//    I.chooseNextStep(nextStepName);
//    I.selectCaseMatches(caseRef, caseMatchesConfig);
//    I.enterCaseMatchesComment(caseRef, caseMatchesCommentSummaryConfig);
      endState = ''
//    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
//    I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);
////    I.seeCaseDetails(caseRef, caseMatchesTabConfig, createCaveatConfig);

//    I.chooseNextStep('Upload document');
//    I.uploadDocument(caseRef);
//    I.enterEventSummary(caseRef, documentUploadSummaryConfig);
//    I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);
////    I.seeCaseDetails(caseRef, documentsTabConfig, createCaveatConfig);

//    I.chooseNextStep('Amend will lodgement');
//    I.enterWillLodgementPage1('update');
//    I.enterWillLodgementPage2('update');
//    I.enterWillLodgementPage3('update');
//    I.checkMyAnswers();
//    I.seeCaseDetails(caseRef, generalDetailsUpdateTabConfig, createWillLodgementConfig);
//    I.seeCaseDetails(caseRef, testatorUpdateTabConfig, createWillLodgementConfig);
//    I.seeCaseDetails(caseRef, executorUpdateTabConfig, createWillLodgementConfig);

//    I.chooseNextStep('Add comment');
//    I.enterComment(caseRef, commentSummaryConfig);
//    I.seeCaseDetails(caseRef, historyTabConfig, commentSummaryConfig);

//    I.chooseNextStep('Generate deposit receipt');
//    I.generateDepositReceipt(caseRef, generateDepositReceiptSummaryConfig);
//    I.seeCaseDetails(caseRef, generateDepositReceiptTabConfig, generateDepositReceiptSummaryConfig);

//    I.chooseNextStep('Withdraw will');
//    I.selectWithdrawalReason(caseRef);
//    I.enterWithdrawalSummary(caseRef, withdrawalSummaryConfig);
//    I.seeCaseDetails(caseRef, historyTabConfig, withdrawalSummaryConfig);

}).retry(testConfig.TestRetryScenarios);
