'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

// const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/grantOfProbate/caseMatchesConfig');
const createGrantOfProbateConfig = require('src/test/end-to-end/pages/createGrantOfProbate/createGrantOfProbateConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/grantOfProbate/documentUploadConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfig');
// const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseMatchesTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const paymentDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/paymentDetailsTabConfig');

const applicantDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsUpdateTabConfig');
const caseDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsUpdateTabConfig');
const deceasedUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedUpdateTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Grant of Probate Workflow - E2E test 01 - Grant of Representation for a Personal Applicant - Apply for grant of representation -> Withdraw application', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    let nextStepName = 'PA1P/PA1A/Solicitors';
    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor);
    I.enterGrantOfProbatePage1('create');
    I.enterGrantOfProbatePage2('create');
    I.enterGrantOfProbatePage3('create');
    I.enterGrantOfProbatePage4('create');
    I.enterGrantOfProbatePage5('create');
    I.enterGrantOfProbatePage6('create');
    I.enterGrantOfProbatePage7('create');
    I.enterGrantOfProbatePage8('create');
    I.enterGrantOfProbatePage9('create');
    I.checkMyAnswers(nextStepName);
    let endState = 'Case created';

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop()
        .match(/.{4}/g)
        .join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, deceasedTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, caseDetailsTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, applicantDetailsTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, paymentDetailsTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Handle supplementary evidence';
    I.chooseNextStep(nextStepName);
    I.handleEvidence(caseRef);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case created';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Amend case details';
    I.chooseNextStep(nextStepName);
    I.enterGrantOfProbatePage1('update');
    I.checkMyAnswers(nextStepName);
    I.chooseNextStep(nextStepName);
    I.enterGrantOfProbatePage2('update');
    I.checkMyAnswers(nextStepName);
    I.chooseNextStep(nextStepName);
    I.enterGrantOfProbatePage3('update');
    I.checkMyAnswers(nextStepName);
    I.chooseNextStep(nextStepName);
    I.enterGrantOfProbatePage4('update');
    I.checkMyAnswers(nextStepName);
    I.chooseNextStep(nextStepName);
    I.enterGrantOfProbatePage5('update');
    I.checkMyAnswers(nextStepName);
    I.chooseNextStep(nextStepName);
    I.enterGrantOfProbatePage6('update');
    I.checkMyAnswers(nextStepName);
    I.chooseNextStep(nextStepName);
    I.enterGrantOfProbatePage7('update');
    I.checkMyAnswers(nextStepName);
    I.chooseNextStep(nextStepName);
    I.enterGrantOfProbatePage8('update');
    I.checkMyAnswers(nextStepName);

    // I.chooseNextStep(nextStepName);
    // I.enterGrantOfProbatePage9('update');
    // I.checkMyAnswers(nextStepName);

    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, deceasedUpdateTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, caseDetailsUpdateTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Print the case';
    I.chooseNextStep(nextStepName);
    I.printCase(caseRef);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting documentation';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Add Comment';
    I.chooseNextStep(nextStepName);
    I.enterComment(caseRef, nextStepName);
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Upload Documents';
    I.chooseNextStep(nextStepName);
    I.uploadDocument(caseRef, documentUploadConfig);
    I.enterEventSummary(caseRef, nextStepName);
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    nextStepName = 'Mark as ready for examination';
    I.chooseNextStep(nextStepName);
    I.markAsReadyForExamination(caseRef);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Ready for examination';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    // nextStepName = 'Find matches (Examining)';
    // I.chooseNextStep(nextStepName);
    // I.selectCaseMatchesForGrantOfProbate(caseRef, caseMatchesConfig);
    // I.enterEventSummary(caseRef, nextStepName);
    // I.seeCaseDetails(caseRef, historyTabConfig, ceventSummaryConfig, nextStepName, endState);
    // I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Withdraw application';
    I.chooseNextStep(nextStepName);
    I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case closed';
    I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

}).retry(testConfig.TestRetryScenarios);
