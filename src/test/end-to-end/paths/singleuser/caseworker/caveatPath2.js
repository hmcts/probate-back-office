'use strict';
const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');

const createCaveatConfig = require('src/test/end-to-end/pages/createCaveat/createCaveatConfig');
const emailCaveatorConfig = require('src/test/end-to-end/pages/emailNotifications/caveat/emailCaveatorConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caveat/caseMatchesConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/caveat/documentUploadConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/historyTabConfig');

const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseDetailsTabConfig');
const deceasedDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/deceasedDetailsTabConfig');
const caveatorDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatorDetailsTabConfig');
const caveatDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatDetailsTabConfig');

const caseDetailsTabUpdateConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseDetailsTabUpdateConfig');
const deceasedDetailsTabUpdateConfig = require('src/test/end-to-end/pages/caseDetails/caveat/deceasedDetailsTabUpdateConfig');
const caveatorDetailsTabUpdateConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatorDetailsTabUpdateConfig');
const caveatDetailsTabUpdateConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatDetailsTabUpdateConfig');

const documentsTabEmailCaveatorConfig = require('src/test/end-to-end/pages/caseDetails/caveat/documentsTabEmailCaveatorConfig');
// this check has been removed as a temporary measure 14/01/2020, due to an Elastic Search bug
// const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseMatchesTabConfig');
const documentsTabUploadDocumentConfig = require('src/test/end-to-end/pages/caseDetails/caveat/documentsTabUploadDocumentConfig');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Caseworker Caveat2 - Request appearance';
Scenario(scenarioName, async function ({I}) {

    // BO Caveat (Personal): Raise a caveat -> Caveat not matched -> Request appearance

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now();

    await I.logInfo(scenarioName, 'Login as Caseworker');
    await I.authenticateWithIdamIfAvailable(false);


    // FIRST case is only needed for case-matching with SECOND one

    let nextStepName = 'Raise a caveat';
    await I.logInfo(scenarioName, nextStepName);
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
    await I.enterCaveatPage1('create');
    await I.enterCaveatPage2('create', unique_deceased_user);
    await I.enterCaveatPage3('create');
    await I.enterCaveatPage4('create');
    await I.checkMyAnswers(nextStepName);
    let endState;

    // SECOND case - the main test case

    await I.logInfo(scenarioName, nextStepName);
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
    await I.enterCaveatPage1('create');
    await I.enterCaveatPage2('create', unique_deceased_user);
    await I.enterCaveatPage3('create');
    await I.enterCaveatPage4('create');
    await I.checkMyAnswers(nextStepName);
    endState = 'Caveat raised';

    const caseRef = await I.getCaseRefFromUrl();

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, createCaveatConfig);
    await I.seeCaseDetails(caseRef, deceasedDetailsTabConfig, createCaveatConfig);
    await I.seeCaseDetails(caseRef, caveatorDetailsTabConfig, createCaveatConfig);
    // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
    createCaveatConfig.caveat_expiry_date = dateFns.format(legacyParse(dateFns.addMonths(new Date(), 6)), convertTokens('D MMM YYYY'));
    await I.seeCaseDetails(caseRef, caveatDetailsTabConfig, createCaveatConfig);

    nextStepName = 'Caveat match';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.selectCaseMatchesForCaveat(caseRef, nextStepName, true, caseMatchesConfig.addNewButton);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat matching';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    // this check has been removed as a temporary measure 14/01/2020, due to an Elastic Search bug
    // await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Email caveator'; // When in state 'Caveat matching'
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.emailCaveator(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when emailing the caveator.
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When emailing the caveator, the Date added for the email document is set to today
    emailCaveatorConfig.dateAdded = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
    await I.seeCaseDetails(caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);

    nextStepName = 'Caveat not matched';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat not matched';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

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

    nextStepName = 'Await caveat resolution';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting caveat resolution';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Warning requested';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Warning validation';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Issue caveat warning';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting warning response';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Request appearance';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Review appearance';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Amend caveat details';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterCaveatPage1('update');
    await I.enterCaveatPage2('update', unique_deceased_user);
    await I.enterCaveatPage3('update');
    await I.enterEventSummary(caseRef, nextStepName);

    // Note that End State does not change when amending the caveat details.
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseDetailsTabUpdateConfig, createCaveatConfig);
    await I.seeCaseDetails(caseRef, deceasedDetailsTabUpdateConfig, createCaveatConfig);
    await I.seeCaseDetails(caseRef, caveatorDetailsTabUpdateConfig, createCaveatConfig);
    await I.seeCaseDetails(caseRef, caveatDetailsTabUpdateConfig, createCaveatConfig);

    nextStepName = 'Withdraw caveat';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.withdrawCaveatPage1();
    await I.enterEventSummary(caseRef, nextStepName);

    endState = 'Caveat closed';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    await I.signOut();

}).retry(testConfig.TestRetryScenarios);
