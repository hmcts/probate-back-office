'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
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
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseMatchesTabConfig');
const documentsTabUploadDocumentConfig = require('src/test/end-to-end/pages/caseDetails/caveat/documentsTabUploadDocumentConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('01 BO Caveat E2E - Order summons', async function (I) {

    // BO Caveat (Personal): Raise a caveat -> Caveat not matched -> Order summons

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now();

    // IdAM
    await I.authenticateWithIdamIfAvailable();

    // FIRST case is only needed for case-matching with SECOND one

    let nextStepName = 'Raise a caveat';
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
    await I.enterCaveatPage1('create');
    await I.enterCaveatPage2('create', unique_deceased_user);
    await I.enterCaveatPage3('create');
    await I.enterCaveatPage4('create');
    await I.checkMyAnswers(nextStepName);
    let endState = 'Caveat raised';

    // SECOND case - the main test case

    nextStepName = 'Raise a caveat';
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
    await I.enterCaveatPage1('create');
    await I.enterCaveatPage2('create', unique_deceased_user);
    await I.enterCaveatPage3('create');
    await I.enterCaveatPage4('create');
    await I.checkMyAnswers(nextStepName);
    endState = 'Caveat raised';

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/')
        .pop()
        .match(/.{4}/g)
        .join('-');

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, createCaveatConfig);
    await I.seeCaseDetails(caseRef, deceasedDetailsTabConfig, createCaveatConfig);
    await I.seeCaseDetails(caseRef, caveatorDetailsTabConfig, createCaveatConfig);

    // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
    createCaveatConfig.caveat_expiry_date = dateFns.format(dateFns.addMonths(new Date(), 6), 'D MMM YYYY');
    await I.seeCaseDetails(caseRef, caveatDetailsTabConfig, createCaveatConfig);

    nextStepName = 'Email caveator'; // When in state 'Caveat raised'
    await I.chooseNextStep(nextStepName);
    await I.emailCaveator(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when emailing the caveator.
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When emailing the caveator, the Date added for the email document is set to today
    emailCaveatorConfig.dateAdded = dateFns.format(new Date(), 'D MMM YYYY');
    await I.seeCaseDetails(caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);

    nextStepName = 'Caveat match';
    await I.chooseNextStep(nextStepName);
    await I.selectCaseMatchesForCaveat(caseRef, caseMatchesConfig, nextStepName, true, true);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat matching';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Caveat not matched';
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat not matched';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Upload document';
    await I.chooseNextStep(nextStepName);
    await I.uploadDocument(caseRef, documentUploadConfig);
    await I.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when uploading a document.
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, documentsTabUploadDocumentConfig, documentUploadConfig);

    nextStepName = 'Add comment';
    await I.chooseNextStep(nextStepName);
    await I.enterComment(caseRef, nextStepName);
    // Note that End State does not change when adding a comment.
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Await caveat resolution';
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting caveat resolution';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Warning requested';
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Warning validation';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Issue caveat warning';
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting warning response';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Order summons';
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Summons ordered';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Amend caveat details';
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
    await I.chooseNextStep(nextStepName);
    await I.withdrawCaveatPage1();
    await I.enterEventSummary(caseRef, nextStepName);

    endState = 'Caveat closed';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    await I.click('#sign-out');

}).retry(testConfig.TestRetryScenarios);
