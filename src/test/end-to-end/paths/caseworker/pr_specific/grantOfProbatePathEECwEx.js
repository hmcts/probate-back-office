'use strict';
const dateFns = require('date-fns');
const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/grantOfProbate/caseMatchesConfigEE');
const createGrantOfProbateConfig = require('src/test/end-to-end/pages/createGrantOfProbateManual/createGrantOfProbateManualConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/grantOfProbate/documentUploadConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const issueGrantConfig = require('src/test/end-to-end/pages/issueGrant/issueGrantConfig');
const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfigEE');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseMatchesTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfigEE');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const grantNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/grantNotificationsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/copiesTabConfig');
const ihtTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/ihtTabConfig');
const ihtTabConfigUpdate = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/ihtUpdateTabConfig');
const nextStepConfig = require('src/test/end-to-end/pages/nextStep/nextStepConfig.json');
const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Experience Caseworker';
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
    await I.enterGrantOfProbateManualPage1('create', unique_deceased_user, createGrantOfProbateConfig.page1_deceasedDod_year);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage2');
    await I.enterGrantOfProbateManualPage2('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage3');
    await I.enterGrantOfProbateManualPage3('create');
    await I.checkMyAnswers(nextStepName);
    let endState;

    // SECOND case - the main test case

    await I.logInfo(scenarioName, nextStepName + ' - second case');
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage1');
    await I.enterGrantOfProbateManualPage1('create', unique_deceased_user, createGrantOfProbateConfig.page1_deceasedDod_year);
    await I.logInfo(scenarioName, 'enterGrantOfProbateManualPage2');
    await I.enterGrantOfProbateManualPage2('create');
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
    await I.seeCaseDetails(caseRef, ihtTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Handle supplementary evidence';
    await I.logInfo(scenarioName, nextStepConfig, caseRef);
    await I.chooseNextStep(nextStepConfig.handleSupEvidence);
    await I.handleEvidence(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    //    await I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Add Comment';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.addComment);
    await I.enterComment(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Upload Documents';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.uploadDocument);
    await I.uploadDocument(caseRef, documentUploadConfig);
    await I.enterEventSummary(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    // "reverting" update back to defaults - to enable case-match with matching case
    nextStepName = 'Amend case details';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.amendCaseDetails);
    await I.enterGrantOfProbatePage4('EE');
    await I.checkMyAnswers(nextStepName);
    await I.seeCaseDetails(caseRef, ihtTabConfigUpdate, createGrantOfProbateConfig);

    nextStepName = 'Generate grant preview';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.generateGrantPreview);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Ready to issue';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Stop case';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.stopCase);
    await I.caseProgressStopEscalateIssueAddCaseStoppedReason();
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Stop case';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Resolve stop';
    const resolveStop = 'Ready to issue';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.resolveStop);
    await I.chooseResolveStop(resolveStop);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Resolve stop';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Find matches (cases)';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepConfig.findMatch);
    await I.selectCaseMatchesForGrantOfProbate(caseRef, nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Find matches (Issue grant)';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

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
});
