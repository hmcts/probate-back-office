'use strict';
const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');

const createWillLodgementConfig = require('src/test/end-to-end/pages/createWillLodgement/createWillLodgementConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/willLodgement/documentUploadConfig');
const generateDepositReceiptConfig = require('src/test/end-to-end/pages/generateDepositReceipt/generateDepositReceiptConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/willLodgement/caseMatchesConfig');
const withdrawWillConfig = require('src/test/end-to-end/pages/withdrawal/willLodgement/withdrawalConfig');

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
const willWithdrawalDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/willLodgement/willWithdrawalDetailsTabConfig');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Caseworker Will Lodgement - Withdraw will';
Scenario(scenarioName, async function ({I}) {

    // BO Will Lodgement (Personal): Create a will lodgement -> Withdraw will

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now();

    await I.logInfo(scenarioName, 'Login as Caseworker');
    await I.authenticateWithIdamIfAvailable(false);

    // FIRST case is only needed for case-matching with SECOND one

    let nextStepName = 'Create a will lodgement';
    await I.logInfo(scenarioName, nextStepName);
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_will, createCaseConfig.list3_text_will);
    await I.enterWillLodgementPage1('create');
    await I.enterWillLodgementPage2('create', unique_deceased_user);
    await I.enterWillLodgementPage3('create');
    await I.checkMyAnswers(nextStepName);

    // SECOND case - the main test case

    await I.logInfo(scenarioName, nextStepName);
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_will, createCaseConfig.list3_text_will);
    await I.enterWillLodgementPage1('create');
    await I.enterWillLodgementPage2('create', unique_deceased_user);
    await I.enterWillLodgementPage3('create');
    await I.checkMyAnswers(nextStepName);
    let endState = 'Will lodgement created';

    await I.wait(testConfig.WillLodgementDelay);
    const caseRef = await I.getCaseRefFromUrl();

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, createWillLodgementConfig);
    await I.seeCaseDetails(caseRef, testatorTabConfig, createWillLodgementConfig);
    await I.seeCaseDetails(caseRef, executorTabConfig, createWillLodgementConfig);

    nextStepName = 'Upload document';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.uploadDocument(caseRef, documentUploadConfig);
    await I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when uploading a document.
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, documentsTabUploadDocumentConfig, documentUploadConfig);

    nextStepName = 'Add comment';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterComment(caseRef, nextStepName);
    // Note that End State does not change when adding a comment.
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Amend will lodgement';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterWillLodgementPage1('update');
    await I.enterWillLodgementPage2('update', unique_deceased_user);
    await I.enterWillLodgementPage3('update');
    await I.checkMyAnswers(nextStepName);
    // Note that End State does not change when amending a Will Lodgement.
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseDetailsTabUpdateConfig, createWillLodgementConfig);
    await I.seeCaseDetails(caseRef, testatorTabUpdateConfig, createWillLodgementConfig);
    await I.seeCaseDetails(caseRef, executorTabUpdateConfig, createWillLodgementConfig);

    nextStepName = 'Generate deposit receipt';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when generating a deposit receipt.
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When generating a deposit receipt, the Date added for the deposit receipt document is set to today
    generateDepositReceiptConfig.dateAdded = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
    await I.seeCaseDetails(caseRef, documentsTabGenerateDepositReceiptConfig, generateDepositReceiptConfig);

    // "reverting" update back to defaults - to enable case-match with matching case
    nextStepName = 'Amend will lodgement';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterWillLodgementPage2('update2orig');
    await I.checkMyAnswers(nextStepName);

    nextStepName = 'Match application';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.selectCaseMatchesForWillLodgement(caseRef, nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Will lodged';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Withdraw will';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.selectWithdrawalReason(caseRef, withdrawWillConfig);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Will withdrawn';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, willWithdrawalDetailsTabConfig, withdrawWillConfig);
    await I.logInfo(scenarioName, endState, caseRef);

    await I.signOut();

}).retry(testConfig.TestRetryScenarios);
