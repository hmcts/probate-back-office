'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');
const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');

const createCaveatConfig = require('src/test/end-to-end/pages/createCaveat/createCaveatConfig');
const emailCaveatorConfig = require('src/test/end-to-end/pages/emailNotifications/caveat/emailCaveatorConfig');
const emailCaveatorSummaryConfig = require('src/test/end-to-end/pages/eventSummary/caveat/emailCaveatorSummaryConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caveat/caseMatchesConfig');
const caseMatchesCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/caveat/caseMatchesCommentSummaryConfig');
const caveatNotMatchedSummaryConfig = require('src/test/end-to-end/pages/eventSummary/caveat/caveatNotMatchedSummaryConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');
const documentUploadSummaryConfig = require('src/test/end-to-end/pages/eventSummary/documentUploadSummaryConfig');
const addCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/addCommentSummaryConfig');
const withdrawCaveatSummaryConfig = require('src/test/end-to-end/pages/eventSummary/caveat/withdrawCaveatSummaryConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/historyTabConfig');

const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseDetailsTabConfig');
const deceasedDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/deceasedDetailsTabConfig');
const caveatorDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatorDetailsTabConfig');
const caveatDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caveatDetailsTabConfig');

const documentsTabEmailCaveatorConfig = require('src/test/end-to-end/pages/caseDetails/caveat/documentsTabEmailCaveatorConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseMatchesTabConfig');
const documentsTabUploadDocumentConfig = require('src/test/end-to-end/pages/caseDetails/caveat/documentsTabUploadDocumentConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Caveat Workflow - E2E Test 04 - Caveat for a Personal Applicant - Raise a caveat -> Caveat not matched -> Withdraw caveat', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    let nextStepName = 'Raise a caveat';
    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_raise_caveat);
    I.enterCaveatPage1('create');
    I.enterCaveatPage2('create');
    I.enterCaveatPage3('create');
    I.checkMyAnswers();
    let endState = 'Caveat raised';
    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop().match(/.{4}/g).join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseDetailsTabConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, deceasedDetailsTabConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, caveatorDetailsTabConfig, createCaveatConfig);
    // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
    createCaveatConfig.caveat_expiry_date = dateFns.format(dateFns.addMonths(new Date(),6),'DD MMM YYYY');
    I.seeCaseDetails(caseRef, caveatDetailsTabConfig, createCaveatConfig);

    nextStepName = 'Caveat match';
    I.chooseNextStep(nextStepName);
    I.selectCaseMatchesForCaveat(caseRef, caseMatchesConfig);
    I.enterCaseMatchesComment(caseRef, caseMatchesCommentSummaryConfig);
    endState = 'Caveat matching';
    I.seeCaseDetails(caseRef, historyTabConfig, caseMatchesCommentSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Caveat not matched';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, caveatNotMatchedSummaryConfig);
    endState = 'Caveat not matched';
    I.seeCaseDetails(caseRef, historyTabConfig, caveatNotMatchedSummaryConfig, nextStepName, endState);

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

    nextStepName = 'Withdraw caveat';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, withdrawCaveatSummaryConfig);
    endState = 'Caveat closed';
    I.seeCaseDetails(caseRef, historyTabConfig, withdrawCaveatSummaryConfig, nextStepName, endState);

    nextStepName = 'Email caveator';   // When in state 'Caveat closed'
    I.chooseNextStep(nextStepName);
    I.emailCaveator(caseRef);
    I.enterEventSummary(caseRef, emailCaveatorSummaryConfig);
    // Note that End State does not change when emailing the caveator.
    I.seeCaseDetails(caseRef, historyTabConfig, emailCaveatorSummaryConfig, nextStepName, endState);
    // When emailing the caveator, the Date added for the email document is set to today
    emailCaveatorConfig.dateAdded = dateFns.format(new Date(), 'DD MMM YYYY');
    I.seeCaseDetails(caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);

    I.click('#sign-out');

}).retry(testConfig.TestRetryScenarios);
