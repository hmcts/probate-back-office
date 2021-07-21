'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/grantOfProbate/caseMatchesConfig');
const createGrantOfProbateConfig = require('src/test/end-to-end/pages/createGrantOfProbateSolicitor/createGrantOfProbateConfig-NonTrust');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/grantOfProbate/documentUploadConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const issueGrantConfig = require('src/test/end-to-end/pages/issueGrant/issueGrantConfig');
const markForExaminationConfig = require('src/test/end-to-end/pages/markForExamination/markForExaminationConfig');
const markForIssueConfig = require('src/test/end-to-end/pages/markForIssue/markForIssueConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/cwCreateGopSol/applicantDetailsTabConfig-NonTrust');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/cwCreateGoPSol/caseDetailsTabConfig-Succ');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseMatchesTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/cwCreateGopSol/deceasedTabConfig');
const docNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/docNotificationsTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const examChecklistTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/examChecklistTabConfig');
const grantNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/grantNotificationsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/copiesTabConfig');

const applicantDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/cwCreateGopSol/applicantDetailsUpdateTabConfig');
const caseDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/cwCreateGopSol/caseDetailsUpdateTabConfig');
const deceasedUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/cwCreateGopSol/deceasedUpdateTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Caseworker Grant of Representation - Sol journey - Non Trust Corp option - Grant issued';
Scenario(scenarioName, async function ({I}) {
    // BO Grant of Representation (Personal): Case created -> Grant issued

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now();

    await I.logInfo(scenarioName, 'Login as Caseworker');
    await I.authenticateWithIdamIfAvailable(false);

    // FIRST case is only needed for case-matching with SECOND one

    let nextStepName = 'PA1P/PA1A/Solicitors';
    await I.logInfo(scenarioName, nextStepName + ' - first case');
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage1');
    await I.cwEnterSolsGoPPage1('create', createGrantOfProbateConfig);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage2');
    await I.cwEnterSolsGoPPage2('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage3');
    await I.cwEnterSolsGoPPage3('create', createGrantOfProbateConfig);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage4');
    await I.cwEnterSolsGoPPage4('create', createGrantOfProbateConfig);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage5');
    await I.cwEnterSolsGoPPage5('create', unique_deceased_user);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage6');
    await I.cwEnterSolsGoPPage6('create', createGrantOfProbateConfig);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage7');
    await I.cwEnterSolsGoPPage7('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage8');
    await I.cwEnterSolsGoPPage8('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage9');
    await I.cwEnterSolsGoPPage9('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage10');
    await I.cwEnterSolsGoPPage10();
    await I.checkMyAnswers(nextStepName);
    let endState;

    // SECOND case - the main test case

    await I.logInfo(scenarioName, nextStepName + ' - second case');
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage1');
    await I.cwEnterSolsGoPPage1('create', createGrantOfProbateConfig);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage2');
    await I.cwEnterSolsGoPPage2('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage3');
    await I.cwEnterSolsGoPPage3('create', createGrantOfProbateConfig);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage4');
    await I.cwEnterSolsGoPPage4('create', createGrantOfProbateConfig);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage5');
    await I.cwEnterSolsGoPPage5('create', unique_deceased_user);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage6');
    await I.cwEnterSolsGoPPage6('create', createGrantOfProbateConfig);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage7');
    await I.cwEnterSolsGoPPage7('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage8');
    await I.cwEnterSolsGoPPage8('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage9');
    await I.cwEnterSolsGoPPage9('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage10');
    await I.cwEnterSolsGoPPage10();
    await I.checkMyAnswers(nextStepName);
    endState = 'Case created';

    const caseRef = await I.getCaseRefFromUrl();

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, createGrantOfProbateConfig);
    await I.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, copiesTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Handle supplementary evidence';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.handleEvidence(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case created';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Amend case details';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage1');
    await I.cwEnterSolsGoPPage1('update', createGrantOfProbateConfig);
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage4');
    await I.cwEnterSolsGoPPage4('update', createGrantOfProbateConfig);
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage5');
    await I.cwEnterSolsGoPPage5('update', unique_deceased_user);
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage6');
    await I.cwEnterSolsGoPPage6('update', createGrantOfProbateConfig);
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage7');
    await I.cwEnterSolsGoPPage7('update');
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage8');
    await I.cwEnterSolsGoPPage8('update');
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage9');
    await I.cwEnterSolsGoPPage9('update');
    await I.checkMyAnswers(nextStepName);

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    createGrantOfProbateConfig.page8_deceasedDomicileInEngWales = 'No';
    await I.seeCaseDetails(caseRef, deceasedUpdateTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, caseDetailsUpdateTabConfig, createGrantOfProbateConfig);
    await I.dontSeeCaseDetails(caseDetailsUpdateTabConfig.fieldsNotPresent);
    await I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, createGrantOfProbateConfig);

    nextStepName = 'Print the case';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.printCase(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Awaiting documentation';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Add Comment';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterComment(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Upload Documents';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.uploadDocument(caseRef, documentUploadConfig);
    await I.enterEventSummary(caseRef, nextStepName);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    nextStepName = 'Mark as ready for examination';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.markForExamination(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Ready for examination';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When sending a notification, the Date added for the notification is set to today
    markForExaminationConfig.date = dateFns.format(new Date(), 'd MMM yyyy');
    await I.seeCaseDetails(caseRef, docNotificationsTabConfig, markForExaminationConfig);

    // "reverting" update back to defaults - to enable case-match with matching case
    nextStepName = 'Amend case details';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.cwEnterSolsGoPPage5('update2orig');
    await I.checkMyAnswers(nextStepName);

    nextStepName = 'Find matches (Examining)';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.selectCaseMatchesForGrantOfProbate(caseRef, nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case Matching (Examining)';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Examine case';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Examining';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Mark as ready to issue';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.markForIssue(caseRef);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Ready to issue';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

    nextStepName = 'Find matches (Issue grant)';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.selectCaseMatchesForGrantOfProbate(caseRef, nextStepName);
    await I.enterEventSummary(caseRef, nextStepName);
    endState = 'Case Matching (Issue grant)';
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    nextStepName = 'Issue grant';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.issueGrant(caseRef);
    endState = 'Grant issued';
    await I.logInfo(scenarioName, endState, caseRef);
    await I.enterEventSummary(caseRef, nextStepName);

    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    // When sending an email notification, the Date added for the email notification is set to today
    issueGrantConfig.date = dateFns.format(new Date(), 'd MMM yyyy');
    await I.seeCaseDetails(caseRef, grantNotificationsTabConfig, issueGrantConfig);
    await I.seeCaseDetails(caseRef, examChecklistTabConfig, markForIssueConfig);

}).retry(testConfig.TestRetryScenarios);
