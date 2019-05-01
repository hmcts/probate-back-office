'use strict';

const dateFns = require('date-fns');
const caveatorDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/legacyCaveatorDetailsTabConfig');
const deceasedDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/legacyDeceasedDetailsTabConfig');
const createCaveatConfig = require('src/test/end-to-end/pages/createCaveat/createCaveatConfig');
const testConfig = require('src/test/config');
const filterCaseConfig = require('src/test/end-to-end/pages/filterCase/filterCaseConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/historyTabConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caveat/legacyCaseMatchesConfig');
const legacyCaseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/legacyCaseMatchesTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Legacy search', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    let nextStepName = 'Amend case details for import';
    I.filterCase(filterCaseConfig.list1_text, filterCaseConfig.list2_text, filterCaseConfig.list3_text);
    I.selectCase();
    I.legacyCaseSearch();
    I.legacyCaseSearch2();
    I.legacyCaseSearch3();
    I.caseSearch("Caveat");
    let endState = 'Caveat imported';
    I.openCaveatCase();
    const url = await I.grabCurrentUrl();
    I.wait(10);
    const caseRef = url.split('/').pop().match(/.{4}/g).join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    //if(I.clickIfVisible(caseDetailsTabConfig.tabName)){
    //    I.seeCaseDetails(caseRef, caseDetailsTabConfig, createCaveatConfig);
    //}
    I.seeCaseDetails(caseRef, deceasedDetailsTabConfig, createCaveatConfig);
    I.seeCaseDetails(caseRef, caveatorDetailsTabConfig, createCaveatConfig);
//    // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
    createCaveatConfig.caveat_expiry_date = dateFns.format(dateFns.addMonths(new Date(),6),'D MMM YYYY');
//    I.seeCaseDetails(caseRef, caveatDetailsTabConfig, createCaveatConfig);
//
    nextStepName = 'Amend case details for import';   // When in state 'Caveat raised'
    I.chooseNextStep(nextStepName);
    I.enterCaveatPage1('import');
    I.enterCaveatPage2('import');
    I.enterCaveatPage3('import');
    I.enterCaveatPage4('import');
    I.enterCaveatPage5('import');

    // TODO: add the email address when amending a case to use this functionality
    //nextStepName = 'Email caveator';   // When in state 'Caveat raised'
    //I.chooseNextStep(nextStepName);
    //I.emailCaveator(caseRef);
    //I.enterEventSummary(caseRef, nextStepName);

//    // Note that End State does not change when emailing the caveator.
//    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
//    // When emailing the caveator, the Date added for the email document is set to today
//    emailCaveatorConfig.dateAdded = dateFns.format(new Date(), 'D MMM YYYY');
//    I.seeCaseDetails(caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);
//
    nextStepName = 'Caveat match';
    I.chooseNextStep(nextStepName);
    I.selectCaseMatchesForCaveat(caseRef, caseMatchesConfig);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat matching';
    nextStepName = 'Caveat not matched';
//    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
//    I.seeCaseDetails(caseRef, legacyCaseMatchesTabConfig, caseMatchesConfig);
//
    nextStepName = 'Caveat not matched';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Caveat not matched';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
//
//    nextStepName = 'Upload document';
//    I.chooseNextStep(nextStepName);
//    I.uploadDocument(caseRef);
//    I.enterEventSummary(caseRef, nextStepName);
//    // Note that End State does not change when uploading a document.
//    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
//    I.seeCaseDetails(caseRef, documentsTabUploadDocumentConfig, documentUploadConfig);
//
//    nextStepName = 'Add comment';
//    I.chooseNextStep(nextStepName);
//    I.enterComment(caseRef, nextStepName);
//    // Note that End State does not change when adding a comment.
//    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
//
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
//
//    nextStepName = 'Amend caveat details';
//    I.chooseNextStep(nextStepName);
//    I.enterCaveatPage1('update');
//    I.enterCaveatPage2('update');
//    I.enterCaveatPage3('update');
//    I.enterCaveatPage4('update');
//    I.enterEventSummary(caseRef, nextStepName);
//    // Note that End State does not change when amending the caveat details.
//    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
//    I.seeCaseDetails(caseRef, caseDetailsTabUpdateConfig, createCaveatConfig);
//    I.seeCaseDetails(caseRef, deceasedDetailsTabUpdateConfig, createCaveatConfig);
//    I.seeCaseDetails(caseRef, caveatorDetailsTabUpdateConfig, createCaveatConfig);
//    I.seeCaseDetails(caseRef, caveatDetailsTabUpdateConfig, createCaveatConfig);

    I.click('#sign-out');


}).retry(testConfig.TestRetryScenarios);