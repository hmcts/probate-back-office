'use strict';
const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/grantOfProbate/caseMatchesConfigEE');
const createGrantOfProbateManualProbateManCaseConfig = require('src/test/end-to-end/pages/createGrantOfProbateManual/createGrantOfProbateManualProbateManCaseConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/grantOfProbate/documentUploadConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const issueGrantConfig = require('src/test/end-to-end/pages/issueGrant/issueGrantConfig');
const markForExaminationConfig = require('src/test/end-to-end/pages/markForExamination/markForExaminationConfig');
const markForIssueConfig = require('src/test/end-to-end/pages/markForIssue/markForIssueConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfigEE');

const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseMatchesTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfigEE');
const docNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/docNotificationsTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const examChecklistTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/examChecklistTabConfig');
const grantNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/grantNotificationsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/copiesTabConfig');
const ihtTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/ihtTabConfig');
const ihtTabConfigUpdate = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/ihtUpdateTabConfig');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Caseworker Grant of Representation - probateman case match';
Scenario(scenarioName, async function ({I}) {
    // Using the deceased user details from existing legacy database Probateman
    const unique_deceased_user = 'No';
    let endState;
    await I.logInfo(scenarioName, 'Login as Caseworker');
    await I.authenticateWithIdamIfAvailable(false);

    // Create case with same deceased details from legacy database

    let nextStepName = 'PA1P/PA1A/Solicitors Manual';
    await I.logInfo(scenarioName, nextStepName + ' - first case');
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage1');
    await I.enterGrantOfProbateManualPage1('create', unique_deceased_user, createGrantOfProbateManualProbateManCaseConfig);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage2');
    await I.enterGrantOfProbateManualPage2('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage3');
    await I.enterGrantOfProbateManualPage3('create');
    await I.checkMyAnswers(nextStepName);
    endState = 'Case created';

    const caseRef = await I.getCaseRefFromUrl();

    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, createGrantOfProbateManualProbateManCaseConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, createGrantOfProbateManualProbateManCaseConfig);
    await I.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, createGrantOfProbateManualProbateManCaseConfig);
    await I.seeCaseDetails(caseRef, copiesTabConfig, createGrantOfProbateManualProbateManCaseConfig);
    await I.seeCaseDetails(caseRef, ihtTabConfig, createGrantOfProbateManualProbateManCaseConfig);

    nextStepName = 'Print the case';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.printCase(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting documentation';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Mark as ready for examination';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.markForExamination(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Ready for examination';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When sending a notification, the Date added for the notification is set to today
    markForExaminationConfig.date = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
    await I.seeCaseDetails(caseRef, docNotificationsTabConfig, markForExaminationConfig);

    nextStepName = 'Find matches (Examining)';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.selectCaseMatchesForGrantOfProbate(caseRef, nextStepName, false);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case Matching (Examining)';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

}).retry(testConfig.TestRetryScenarios);
