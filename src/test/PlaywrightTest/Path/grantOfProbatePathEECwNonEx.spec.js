// @ts-check
const {test} = require('../Fixtures/fixtures');
const dateFns = require('date-fns');

const createCaseConfig = require('../Pages/createCase/createCaseConfig.json');

const caseMatchesConfig = require('../Pages/caseMatches/grantOfProbate/caseMatchesConfigBeforeSwitchDate');
const createGrantOfProbateConfig = require('../Pages/createGrantOfProbateManual/createGrantOfProbateManualConfig');
const documentUploadConfig = require('../Pages/documentUpload/grantOfProbate/documentUploadConfig');
const eventSummaryConfig = require('../Pages/eventSummary/eventSummaryConfig');
const issueGrantConfig = require('../Pages/issueGrant/issueGrantConfig');
const applicantDetailsTabConfig = require('../Pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE');
const caseDetailsTabConfig = require('../Pages/caseDetails/grantOfProbate/caseDetailsTabConfigBeforeSwitchDate');
const caseMatchesTabConfig = require('../Pages/caseDetails/grantOfProbate/caseMatchesTabConfig');
const deceasedTabConfig = require('../Pages/caseDetails/grantOfProbate/deceasedTabConfigEE400');
const documentUploadTabConfig = require('../Pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const grantNotificationsTabConfig = require('../Pages/caseDetails/grantOfProbate/grantNotificationsTabConfig');
const historyTabConfig = require('../Pages/caseDetails/grantOfProbate/historyTabConfig');
const copiesTabConfig = require('../Pages/caseDetails/grantOfProbate/copiesTabConfig');
const nextStepConfig = require('../Pages/nextStep/nextStepConfig.json');
const registrarsDecisionConfig = require('../Pages/caseDetails/grantOfProbate/registrarsDecisionConfig');
const caseProgressConfig = require('../Pages/caseProgressStandard/caseProgressConfig');
const registrarsDecisionTabConfig = require('../Pages/caseDetails/grantOfProbate/registrarsDecisionTabConfig');
const newConfig = require('../Pages/caseDetails/grantOfProbate/superUserCwConfig.json');
const deceasedTabConfigUpdated = require('../Pages/caseDetails/grantOfProbate/deceasedTabConfigChangeDOB');
const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

test.describe('Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Experience Caseworker', () => {
    test('Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Experience Caseworker',
        async ({basePage, signInPage, createCasePage, cwEventActionsPage}) => {
            const scenarioName = 'Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Non Experience Caseworker';
            // BO Grant of Representation (Personal): Case created -> Grant issued

            // get unique suffix for names - in order to match only against 1 case
            const unique_deceased_user = Date.now();

            await basePage.logInfo(scenarioName, 'Login as Caseworker');
            await signInPage.authenticateWithIdamIfAvailable('superUser');

            // FIRST case is only needed for case-matching with SECOND one

            let nextStepName = 'PA1P/PA1A/Solicitors Manual';
            await basePage.logInfo(scenarioName, nextStepName + ' - first case');
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage1');
            await createCasePage.enterGrantOfProbateManualPage1('create', unique_deceased_user, createGrantOfProbateConfig.page1_deceasedDod_year_update);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage2');
            await createCasePage.enterGrantOfProbateManualPage2('createIHT400');
            await createCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage3');
            await createCasePage.enterGrantOfProbateManualPage3('create');
            await createCasePage.checkMyAnswers(nextStepName);
            let endState;

            // SECOND case - the main test case

            await basePage.logInfo(scenarioName, nextStepName + ' - second case');
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage1');
            await createCasePage.enterGrantOfProbateManualPage1('create', unique_deceased_user, createGrantOfProbateConfig.page1_deceasedDod_year_update);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage2');
            await createCasePage.enterGrantOfProbateManualPage2('createIHT400');
            await createCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage3');
            await createCasePage.enterGrantOfProbateManualPage3('create');
            await createCasePage.checkMyAnswers(nextStepName);
            endState = 'Awaiting documentation';

            const caseRef = await basePage.getCaseRefFromUrl();

            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, deceasedTabConfig, createGrantOfProbateConfig);
            await basePage.seeCaseDetails(caseRef, caseDetailsTabConfig, createGrantOfProbateConfig);
            await basePage.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
            await basePage.seeCaseDetails(caseRef, applicantDetailsTabConfig, createGrantOfProbateConfig);
            await basePage.seeCaseDetails(caseRef, copiesTabConfig, createGrantOfProbateConfig);
            //await I.seeCaseDetails(caseRef, ihtTabConfig, createGrantOfProbateConfig);

            nextStepName = 'Registrar\'s decision';
            await basePage.logInfo(scenarioName, nextStepConfig, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.registrarsDecision(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            await basePage.seeCaseDetails(caseRef, registrarsDecisionTabConfig, registrarsDecisionConfig);

            nextStepName = 'Handle supplementary evidence';
            await basePage.logInfo(scenarioName, nextStepConfig, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.handleSupEvidence);
            await cwEventActionsPage.handleEvidence(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            //    await I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, createGrantOfProbateConfig);

            nextStepName = 'Upload Documents';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.uploadDocument);
            await cwEventActionsPage.uploadDocument(caseRef, documentUploadConfig);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

            nextStepName = 'Select for QA';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.selectForQa);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Case selected for QA';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Fail QA';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.failQa);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Case stopped';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Resolve stop';
            const resolveStop ='Case selected for QA';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.resolveStop);
            await cwEventActionsPage.chooseResolveStop(resolveStop);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Case selected for QA';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Change state';
            endState = 'Awaiting documentation';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.chooseNewState(endState);
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Generate grant preview';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.generateGrantPreview);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Ready to issue';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Find matches (Issue grant)';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.findMatchesIssueGrant);
            await cwEventActionsPage.selectCaseMatches(caseRef, nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Case Matching (Issue grant)';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

            nextStepName = 'Change DOB';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterNewDob(newConfig.newDob);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            await basePage.seeCaseDetails(caseRef, deceasedTabConfigUpdated, createGrantOfProbateConfig);
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Issue grant';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.issueGrant);
            await cwEventActionsPage.issueGrant(caseRef);
            endState = 'Grant issued';
            await basePage.logInfo(scenarioName, endState, caseRef);

            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);

            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // When sending an email notification, the Date added for the email notification is set to today
            issueGrantConfig.date = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
            await basePage.seeCaseDetails(caseRef, grantNotificationsTabConfig, issueGrantConfig);

            nextStepName = 'Post Grant Issue';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.postGrantIssue);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Post grant issued';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Resolve Post Grant Issue';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.resolvePostGrantIssue);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Grant issued';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            issueGrantConfig.date = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
            await basePage.seeCaseDetails(caseRef, grantNotificationsTabConfig, issueGrantConfig);
        });
});
