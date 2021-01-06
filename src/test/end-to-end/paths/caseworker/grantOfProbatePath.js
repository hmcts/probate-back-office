'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/grantOfProbate/caseMatchesConfig');
const createGrantOfProbateConfig = require('src/test/end-to-end/pages/createGrantOfProbate/createGrantOfProbateConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/grantOfProbate/documentUploadConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const issueGrantConfig = require('src/test/end-to-end/pages/issueGrant/issueGrantConfig');
const markForExaminationConfig = require('src/test/end-to-end/pages/markForExamination/markForExaminationConfig');
const markForIssueConfig = require('src/test/end-to-end/pages/markForIssue/markForIssueConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseMatchesTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfig');
const docNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/docNotificationsTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const examChecklistTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/examChecklistTabConfig');
const grantNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/grantNotificationsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/copiesTabConfig');

const applicantDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsUpdateTabConfig');
const caseDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsUpdateTabConfig');
const deceasedUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedUpdateTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('01 BO Grant of Representation E2E - Grant issued', async function (I) {
    // BO Grant of Representation (Personal): Case created -> Grant issued

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now();

    // IdAM
    await I.authenticateWithIdamIfAvailable();

    // FIRST case is only needed for case-matching with SECOND one

    let nextStepName = 'PA1P/PA1A/Solicitors';
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor);
    await I.enterGrantOfProbatePage1('create');
    await I.enterGrantOfProbatePage2('create');
    await I.enterGrantOfProbatePage3('create');
    await I.enterGrantOfProbatePage4('create', unique_deceased_user);
    await I.enterGrantOfProbatePage5('create');
    await I.enterGrantOfProbatePage6('create');
    await I.enterGrantOfProbatePage7('create');
    await I.enterGrantOfProbatePage8('create');
    await I.enterGrantOfProbatePage9();
    await I.checkMyAnswers(nextStepName);
    let endState;

    // SECOND case - the main test case

    nextStepName = 'PA1P/PA1A/Solicitors';
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor);
    await I.enterGrantOfProbatePage1('create');
    await I.enterGrantOfProbatePage2('create');
    await I.enterGrantOfProbatePage3('create');
    await I.enterGrantOfProbatePage4('create', unique_deceased_user);
    await I.enterGrantOfProbatePage5('create');
    await I.enterGrantOfProbatePage6('create');
    await I.enterGrantOfProbatePage7('create');
    await I.enterGrantOfProbatePage8('create');
    await I.enterGrantOfProbatePage9();
    await I.checkMyAnswers(nextStepName);
    endState = 'Case created';

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop()
        .match(/.{4}/g)
        .join('-');

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, copiesTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Handle supplementary evidence';
    await I.chooseNextStep(nextStepName);
    await I.handleEvidence(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case created';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Amend case details';
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage1('update');
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage2('update');
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage3('update');
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage4('update', unique_deceased_user);
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage5('update');
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage6('update');
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage7('update');
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage8('update');
    await I.checkMyAnswers(nextStepName);

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedUpdateTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, caseDetailsUpdateTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Print the case';
    await I.chooseNextStep(nextStepName);
    await I.printCase(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting documentation';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Add Comment';
    await I.chooseNextStep(nextStepName);
    await I.enterComment(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Upload Documents';
    await I.chooseNextStep(nextStepName);
    await I.uploadDocument(caseRef, documentUploadConfig);
    await I.enterEventSummary(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    nextStepName = 'Mark as ready for examination';
    await I.chooseNextStep(nextStepName, 10);
    await I.markForExamination(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Ready for examination';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When sending a notification, the Date added for the notification is set to today
    markForExaminationConfig.date = dateFns.format(new Date(), 'D MMM YYYY');
    await I.seeCaseDetails(caseRef, docNotificationsTabConfig, markForExaminationConfig);

    // "reverting" update back to defaults - to enable case-match with matching case
    nextStepName = 'Amend case details';
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage4('update2orig');
    await I.checkMyAnswers(nextStepName);

    nextStepName = 'Find matches (Examining)';
    await I.chooseNextStep(nextStepName);
    await I.selectCaseMatchesForGrantOfProbate(caseRef, caseMatchesConfig, nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case Matching (Examining)';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // case matching amended to remove all case matches to make test re-runnable
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Examine case';
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Examining';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Mark as ready to issue';
    await I.chooseNextStep(nextStepName);
    await I.markForIssue(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Ready to issue';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Find matches (Issue grant)';
    await I.chooseNextStep(nextStepName);
    await I.selectCaseMatchesForGrantOfProbate(caseRef, caseMatchesConfig, nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case Matching (Issue grant)';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // case matching amended to remove all case matches to make test re-runnable
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Issue grant';
    await I.chooseNextStep(nextStepName);
    await I.issueGrant(caseRef);
    endState = 'Grant issued';

    //
    // This is as far as we can currently get locally due to bulk printing issue
    await I.enterEventSummary(caseRef, nextStepName);
    // 
    //

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When sending an email notification, the Date added for the email notification is set to today
    issueGrantConfig.date = dateFns.format(new Date(), 'D MMM YYYY');
    await I.seeCaseDetails(caseRef, grantNotificationsTabConfig, issueGrantConfig);
    await I.seeCaseDetails(caseRef, examChecklistTabConfig, markForIssueConfig);

}).retry(testConfig.TestRetryScenarios);
