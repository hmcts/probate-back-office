'use strict';

//const taskListContent = require('app/resources/en/translation/tasklist');
const TestConfigurator = new (require('src/test/end-to-end/helpers/TestConfigurator'))();
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');

const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const createWillLodgementConfig = require('src/test/end-to-end/pages/createWillLodgement/createWillLodgementConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');
const documentUploadSummaryConfig = require('src/test/end-to-end/pages/eventSummary/documentUploadSummaryConfig');
const commentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/commentSummaryConfig');
const generateDepositReceiptSummaryConfig = require('src/test/end-to-end/pages/eventSummary/generateDepositReceiptSummaryConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caseMatchesConfig');
const caseMatchesCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/caseMatchesCommentSummaryConfig');
const withdrawalSummaryConfig = require('src/test/end-to-end/pages/eventSummary/withdrawalSummaryConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/historyTabConfig');
const generalDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/generalDetailsTabConfig');
const testatorTabConfig = require('src/test/end-to-end/pages/caseDetails/testatorTabConfig');
const executorTabConfig = require('src/test/end-to-end/pages/caseDetails/executorTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/documentUploadTabConfig');
const generalDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/generalDetailsUpdateTabConfig');
const testatorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/testatorUpdateTabConfig');
const executorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/executorUpdateTabConfig');
const generateDepositReceiptTabConfig = require('src/test/end-to-end/pages/caseDetails/generateDepositReceiptTabConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/caseMatchesTabConfig');

Feature('Back Office - Will Lodgement for a Personal Applicant').retry(TestConfigurator.getRetryFeatures());

// eslint complains that the Before/After are not used but they are by codeceptjs
// so we have to tell eslint to not validate these
// eslint-disable-next-line no-undef
/*
Before(() => {
    TestConfigurator.getBefore();
});
*/

// eslint-disable-next-line no-undef
/*
After(() => {
    TestConfigurator.getAfter();
});
*/


Scenario(TestConfigurator.idamInUseText('Multiple Executors'), async function (I) {
    let nextStep = 'Upload document';
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
    I.seeCaseDetails(caseRef, executorTabConfig, createWillLodgementConfig, nextStep);

    I.uploadDocument(caseRef);
    I.enterEventSummary(caseRef, documentUploadSummaryConfig);
    nextStep = 'Amend will lodgement';
    I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig, nextStep);

    I.enterWillLodgementPage1('update');
    I.enterWillLodgementPage2('update');
    I.enterWillLodgementPage3('update');
    I.checkMyAnswers();

    I.seeCaseDetails(caseRef, generalDetailsUpdateTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, testatorUpdateTabConfig, createWillLodgementConfig);
    nextStep='Add comment';
    I.seeCaseDetails(caseRef, executorUpdateTabConfig, createWillLodgementConfig, nextStep);

    I.enterComment(caseRef, commentSummaryConfig);

    nextStep = 'Generate deposit receipt';
    I.seeCaseDetails(caseRef, historyTabConfig, commentSummaryConfig, nextStep);

    I.generateDepositReceipt(caseRef, generateDepositReceiptSummaryConfig);
    nextStep = 'Match application';
    I.seeCaseDetails(caseRef, generateDepositReceiptTabConfig, generateDepositReceiptSummaryConfig, nextStep);

    I.selectCaseMatches(caseRef, caseMatchesConfig);
    I.enterCaseMatchesComment(caseRef, caseMatchesCommentSummaryConfig);
    nextStep = 'Withdraw will';
    I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig, nextStep);

    I.selectWithdrawalReason(caseRef);
    I.enterWithdrawalSummary(caseRef, withdrawalSummaryConfig);
    I.seeCaseDetails(caseRef, historyTabConfig, withdrawalSummaryConfig);

});
