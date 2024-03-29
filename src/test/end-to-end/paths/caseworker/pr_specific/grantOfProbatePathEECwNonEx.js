'use strict';
const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/grantOfProbate/caseMatchesConfigBeforeSwitchDate');
const createGrantOfProbateConfig = require('src/test/end-to-end/pages/createGrantOfProbateManual/createGrantOfProbateManualConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/grantOfProbate/documentUploadConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const issueGrantConfig = require('src/test/end-to-end/pages/issueGrant/issueGrantConfig');
const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfigBeforeSwitchDate');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseMatchesTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfigEE400');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const grantNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/grantNotificationsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/copiesTabConfig');
const nextStepConfig = require('src/test/end-to-end/pages/nextStep/nextStepConfig.json');
const registrarsDecisionConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/registrarsDecisionConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStandard/caseProgressConfig');
const registrarsDecisionTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/registrarsDecisionTabConfig');
const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Non Experience Caseworker';
Scenario(scenarioName, async function ({I}) {
    // BO Grant of Representation (Personal): Case created -> Grant issued

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now();

    await I.logInfo(scenarioName, 'Login as Caseworker');
    await I.authenticateWithIdamIfAvailable(false);

    // FIRST case is only needed for case-matching with SECOND one

    let nextStepName = 'PA1P/PA1A/Solicitors Manual';
    await I.logInfo(scenarioName, nextStepName + ' - first case');
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage1');
    await I.enterGrantOfProbateManualPage1('create', unique_deceased_user, createGrantOfProbateConfig.page1_deceasedDod_year_update);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage2');
    await I.enterGrantOfProbateManualPage2('createIHT400');
    await I.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage3');
    await I.enterGrantOfProbateManualPage3('create');
    await I.checkMyAnswers(nextStepName);
    let endState;

    // SECOND case - the main test case

    await I.logInfo(scenarioName, nextStepName + ' - second case');
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage1');
    await I.enterGrantOfProbateManualPage1('create', unique_deceased_user, createGrantOfProbateConfig.page1_deceasedDod_year_update);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage2');
    await I.enterGrantOfProbateManualPage2('createIHT400');
    await I.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage3');
    await I.enterGrantOfProbateManualPage3('create');
    await I.checkMyAnswers(nextStepName);
    endState = 'Awaiting documentation';

    const caseRef = await I.getCaseRefFromUrl();

    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, createGrantOfProbateConfig);
    await I.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, copiesTabConfig, createGrantOfProbateConfig);
    //await I.seeCaseDetails(caseRef, ihtTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Registrar\'s decision';
    await I.logInfo(scenarioName, nextStepConfig, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.registrarsDecision(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, registrarsDecisionTabConfig, registrarsDecisionConfig);

    nextStepName = 'Handle supplementary evidence';
    await I.logInfo(scenarioName, nextStepConfig, caseRef);
    await I.chooseNextStep(nextStepConfig.handleSupEvidence);
    await I.handleEvidence(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    //    await I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Upload Documents';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.uploadDocument);
    await I.uploadDocument(caseRef, documentUploadConfig);
    await I.enterEventSummary(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    nextStepName = 'Select for QA';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.selectForQa);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case selected for QA';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Fail QA';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.failQa);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case stopped';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Resolve stop';
    const resolveStop ='Case selected for QA';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.resolveStop);
    await I.chooseResolveStop(resolveStop);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Resolve stop';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Generate grant preview';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.generateGrantPreview);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Ready to issue';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Find matches (Issue grant)';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.findMatchesIssueGrant);
    await I.selectCaseMatchesForGrantOfProbate(caseRef, nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case Matching (Issue grant)';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Issue grant';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.issueGrant);
    await I.issueGrant(caseRef);
    endState = 'Grant issued';
    await I.logInfo(scenarioName, endState, caseRef);

    await I.enterEventSummary(caseRef, nextStepName);

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When sending an email notification, the Date added for the email notification is set to today
    issueGrantConfig.date = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
    await I.seeCaseDetails(caseRef, grantNotificationsTabConfig, issueGrantConfig);

    nextStepName = 'Post Grant Issue';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.postGrantIssue);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Post grant issued';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Resolve Post Grant Issue';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.resolvePostGrantIssue);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Grant issued';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    issueGrantConfig.date = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
    await I.seeCaseDetails(caseRef, grantNotificationsTabConfig, issueGrantConfig);

}).retry(testConfig.TestRetryScenarios);
