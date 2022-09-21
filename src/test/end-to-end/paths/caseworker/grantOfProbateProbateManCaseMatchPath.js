'use strict';
const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/grantOfProbate/probateManCaseMatchesConfig');
const createGrantOfProbateManualProbateManCaseConfig = require('src/test/end-to-end/pages/createGrantOfProbateManualForProbateMan/createGrantOfProbateManualProbateManCaseConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const markForExaminationConfig = require('src/test/end-to-end/pages/markForExamination/markForExaminationConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfigProbateMan');

const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/probateManCaseMatchesTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfigEE');
const docNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/docNotificationsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/copiesTabConfig');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Caseworker Grant of Representation - probateman case match';
Scenario(scenarioName, async function ({I}) {
    if (testConfig.TestBackOfficeUrl.includes('demo') || testConfig.TestBackOfficeUrl.includes('aat')) {
    let endState;
    await I.logInfo(scenarioName, 'Login as Caseworker');
    await I.authenticateWithIdamIfAvailable(false);

    // Create case with same deceased details from legacy database

    let nextStepName = 'PA1P/PA1A/Solicitors Manual';
    await I.logInfo(scenarioName, nextStepName + ' - first case');
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualForProbateManPage1');
    await I.enterGrantOfProbateManualForProbateManPage1('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualForProbateManPage2');
    await I.enterGrantOfProbateManualForProbateManPage2('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualForProbateManPage3');
    await I.enterGrantOfProbateManualForProbateManPage3('create');
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
    await I.selectProbateManCaseMatchesForGrantOfProbate(caseRef, nextStepName, false);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case Matching (Examining)';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);
    await I.verifyProbateManCcdCaseNumber();
    }
}).retry(testConfig.TestRetryScenarios);
