'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');

const createCaveatConfig = require('src/test/end-to-end/pages/createCaveat/createCaveatConfig');
const emailCaveatorConfig = require('src/test/end-to-end/pages/emailNotifications/caveat/emailCaveatorConfig');
const reopenCaveatConfig = require('src/test/end-to-end/pages/reopenningCases/caveat/reopenCaveatConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caveat/caseMatchesConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/caveat/documentUploadConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/historyTabConfig');

const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseDetailsTabConfig');
const deceasedDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/deceasedDetailsTabConfig');
const caveatorDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatorDetailsTabConfig');
const caveatDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatDetailsTabConfig');
const caveatDetailsTabReopenConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatDetailsTabReopenConfig');

const documentsTabEmailCaveatorConfig = require('src/test/end-to-end/pages/caseDetails/caveat/documentsTabEmailCaveatorConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseMatchesTabConfig');
const documentsTabUploadDocumentConfig = require('src/test/end-to-end/pages/caseDetails/caveat/documentsTabUploadDocumentConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('03 BO Caveat E2E - Caveat expired', async function (I) {

    // BO Caveat (Personal): Raise a caveat -> Caveat not matched -> Caveat expired

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now();

    // IdAM
    await I.authenticateWithIdamIfAvailable();

    // FIRST case is only needed for case-matching with SECOND one

    let nextStepName = 'Raise a caveat';
    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
    I.enterCaveatPage1('create');
    I.enterCaveatPage2('create', unique_deceased_user);
    I.enterCaveatPage3('create');
    I.enterCaveatPage4('create');
    I.checkMyAnswers(nextStepName);
    let endState = 'Caveat raised';

    // SECOND case - the main test case

    nextStepName = 'Raise a caveat';
    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
    I.enterCaveatPage1('create');
    I.enterCaveatPage2('create', unique_deceased_user);
    I.enterCaveatPage3('create');
    I.enterCaveatPage4('create');
    I.checkMyAnswers(nextStepName);
    endState = 'Caveat raised';

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/')
        .pop()
        .match(/.{4}/g)
        .join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseDetailsTabConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, deceasedDetailsTabConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, caveatorDetailsTabConfig, createCaveatConfig);
    // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
    createCaveatConfig.caveat_expiry_date = dateFns.format(dateFns.addMonths(new Date(), 6), 'D MMM YYYY');
    I.seeCaseDetails(caseRef, caveatDetailsTabConfig, createCaveatConfig);

    nextStepName = 'Caveat match';
    I.chooseNextStep(nextStepName);
    I.selectCaseMatchesForCaveat(caseRef, caseMatchesConfig, nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat matching';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Caveat not matched';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat not matched';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Upload document';
    I.chooseNextStep(nextStepName);
    I.uploadDocument(caseRef, documentUploadConfig);
    I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when uploading a document.
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, documentsTabUploadDocumentConfig, documentUploadConfig);

    nextStepName = 'Add comment';
    I.chooseNextStep(nextStepName);
    I.enterComment(caseRef, nextStepName);
    // Note that End State does not change when adding a comment.
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Caveat expired';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat closed';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Email caveator'; // When in state 'Caveat closed'
    I.chooseNextStep(nextStepName);
    I.emailCaveator(caseRef);
    I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when emailing the caveator.
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When emailing the caveator, the Date added for the email document is set to today
    emailCaveatorConfig.dateAdded = dateFns.format(new Date(), 'D MMM YYYY');
    I.seeCaseDetails(caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);

    nextStepName = 'Reopen caveat'; // When in state 'Caveat closed'
    I.chooseNextStep(nextStepName);
    I.reopenCaveat(caseRef);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat raised';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caveatDetailsTabReopenConfig, reopenCaveatConfig);

    I.click('#sign-out');

}).retry(testConfig.TestRetryScenarios);
