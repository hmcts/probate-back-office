'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');

const createCaveatConfig = require('src/test/end-to-end/pages/createCaveat/createCaveatConfig');
const emailCaveatorConfig = require('src/test/end-to-end/pages/emailNotifications/caveat/emailCaveatorConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caveat/caseMatchesConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');

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
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseMatchesTabConfig');
const documentsTabUploadDocumentConfig = require('src/test/end-to-end/pages/caseDetails/caveat/documentsTabUploadDocumentConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Caveat Workflow - E2E Test 01 - Caveat for a Personal Applicant - Raise a caveat -> Caveat not matched -> Order summons', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    let nextStepName = 'Raise a caveat';
    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_raise_caveat);
    I.enterCaveatPage1('create');
    I.enterCaveatPage2('create');
    I.enterCaveatPage3('create');
    I.checkMyAnswers(nextStepName);
    let endState = 'Caveat raised';
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

    nextStepName = 'Email caveator'; // When in state 'Caveat raised'
    I.chooseNextStep(nextStepName);
    I.emailCaveator(caseRef);
    I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when emailing the caveator.
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When emailing the caveator, the Date added for the email document is set to today
    emailCaveatorConfig.dateAdded = dateFns.format(new Date(), 'D MMM YYYY');
    I.seeCaseDetails(caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);

    nextStepName = 'Caveat match';
    I.chooseNextStep(nextStepName);
    I.selectCaseMatchesForCaveat(caseRef, caseMatchesConfig);
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
    I.uploadDocument(caseRef);
    I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when uploading a document.
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, documentsTabUploadDocumentConfig, documentUploadConfig);

    nextStepName = 'Add comment';
    I.chooseNextStep(nextStepName);
    I.enterComment(caseRef, nextStepName);
    // Note that End State does not change when adding a comment.
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Await caveat resolution';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting caveat resolution';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Warning requested';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Warning validation';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Issue caveat warning';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting warning response';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Order summons';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Summons ordered';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Amend caveat details';
    I.chooseNextStep(nextStepName);
    I.enterCaveatPage1('update');
    I.enterCaveatPage2('update');
    I.enterCaveatPage3('update');
    I.enterCaveatPage4('update');
    I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when amending the caveat details.
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseDetailsTabUpdateConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, deceasedDetailsTabUpdateConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, caveatorDetailsTabUpdateConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, caveatDetailsTabUpdateConfig, createCaveatConfig);

    nextStepName = 'Withdraw caveat';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat closed';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    I.click('#sign-out');

}).retry(testConfig.TestRetryScenarios);
