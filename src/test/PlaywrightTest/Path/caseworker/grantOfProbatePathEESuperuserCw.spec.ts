import dateFns from 'date-fns';
import { test } from '../../Fixtures/fixtures.ts';

import createCaseConfig from '../../Pages/createCase/createCaseConfig.json' with { type: 'json' };

import { testConfig } from '../../Configs/config.ts';
import applicantDetailsTabConfig from '../../Pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE.json' with { type: 'json' };
import caseDetailsTabConfig from '../../Pages/caseDetails/grantOfProbate/caseDetailsTabConfigBeforeSwitchDate.json' with { type: 'json' };
import caseMatchesTabConfig from '../../Pages/caseDetails/grantOfProbate/caseMatchesTabConfig.json' with { type: 'json' };
import copiesTabConfig from '../../Pages/caseDetails/grantOfProbate/copiesTabConfig.json' with { type: 'json' };
import deceasedTabConfigUpdated from '../../Pages/caseDetails/grantOfProbate/deceasedTabConfigChangeDOB.json' with { type: 'json' };
import deceasedTabConfig from '../../Pages/caseDetails/grantOfProbate/deceasedTabConfigEE400.json' with { type: 'json' };
import documentUploadTabConfig from '../../Pages/caseDetails/grantOfProbate/documentUploadTabConfig.json' with { type: 'json' };
import grantNotificationsTabConfig from '../../Pages/caseDetails/grantOfProbate/grantNotificationsTabConfig.json' with { type: 'json' };
import historyTabConfig from '../../Pages/caseDetails/grantOfProbate/historyTabConfig.json' with { type: 'json' };
import registrarsDecisionConfig from '../../Pages/caseDetails/grantOfProbate/registrarsDecisionConfig.json' with { type: 'json' };
import registrarsDecisionTabConfig from '../../Pages/caseDetails/grantOfProbate/registrarsDecisionTabConfig.json' with { type: 'json' };
import newConfig from '../../Pages/caseDetails/grantOfProbate/superUserCwConfig.json' with { type: 'json' };
import caseMatchesConfig from '../../Pages/caseMatches/grantOfProbate/caseMatchesConfigBeforeSwitchDate.json' with { type: 'json' };
import caseProgressConfig from '../../Pages/caseProgressStandard/caseProgressConfig.json' with { type: 'json' };
import createGrantOfProbateConfig from '../../Pages/createGrantOfProbateManual/createGrantOfProbateManualConfig.json' with { type: 'json' };
import documentUploadConfig from '../../Pages/documentUpload/grantOfProbate/documentUploadConfig.json' with { type: 'json' };
import eventSummaryConfig from '../../Pages/eventSummary/eventSummaryConfig.json' with { type: 'json' };
import issueGrantConfig from '../../Pages/issueGrant/issueGrantConfig.json' with { type: 'json' };
import nextStepConfig from '../../Pages/nextStep/nextStepConfig.json' with { type: 'json' };

test.describe('Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Experience Caseworker', () => {
    test('Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Experience Caseworker',
        async ({basePage, signInPage, createCasePage, cwEventActionsPage}, testInfo) => {
            const scenarioName = 'Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Experience Caseworker';
            // BO Grant of Representation (Personal): Case created -> Grant issued

            // get unique suffix for names - in order to match only against 1 case
            const unique_deceased_user = Date.now();

            await basePage.logInfo(scenarioName, 'Login as Caseworker', null);
            await signInPage.authenticateWithIdamIfAvailable('superUser');

            // FIRST case is only needed for case-matching with SECOND one

            let nextStepName = 'PA1P/PA1A/Solicitors Manual';
            await basePage.logInfo(scenarioName, nextStepName + ' - first case', null);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage1', null);
            await createCasePage.enterGrantOfProbateManualPage1('create', createGrantOfProbateConfig, unique_deceased_user, createGrantOfProbateConfig.page1_deceasedDod_year_update);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage2', null);
            await createCasePage.enterGrantOfProbateManualPage2('createIHT400');
            await createCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage3', null);
            await createCasePage.enterGrantOfProbateManualPage3('create', createGrantOfProbateConfig);
            // TODO: Expects 2 arguments
            await createCasePage.checkMyAnswers(nextStepName);
            let endState;

            // SECOND case - the main test case

            await basePage.logInfo(scenarioName, nextStepName + ' - second case', null);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage1', null);
            await createCasePage.enterGrantOfProbateManualPage1('create', createGrantOfProbateConfig, unique_deceased_user, createGrantOfProbateConfig.page1_deceasedDod_year_update);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage2', null);
            await createCasePage.enterGrantOfProbateManualPage2('createIHT400');
            await createCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
            await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualPage3', null);
            await createCasePage.enterGrantOfProbateManualPage3('create', createGrantOfProbateConfig);
            // TODO: Expects 2 arguments
            await createCasePage.checkMyAnswers(nextStepName);
            endState = 'Awaiting documentation';

            const caseRef = await basePage.getCaseRefFromUrl();

            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, deceasedTabConfig, createGrantOfProbateConfig);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabConfig, createGrantOfProbateConfig);
            await basePage.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, applicantDetailsTabConfig, createGrantOfProbateConfig);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, copiesTabConfig, createGrantOfProbateConfig);
            //await I.seeCaseDetails(caseRef, ihtTabConfig, createGrantOfProbateConfig);

            nextStepName = 'Registrar\'s decision';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.registrarsDecision(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, registrarsDecisionTabConfig, registrarsDecisionConfig);

            nextStepName = 'Handle supplementary evidence';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.handleSupEvidence);
            await cwEventActionsPage.handleEvidence(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            //    await I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, createGrantOfProbateConfig);

            nextStepName = 'Upload Documents';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.uploadDocument);
            await cwEventActionsPage.uploadDocument(caseRef, documentUploadConfig);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, documentUploadTabConfig, documentUploadConfig);

            nextStepName = 'Select for QA';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.selectForQa);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Case selected for QA';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Fail QA';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.failQa);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Case stopped';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Resolve stop';
            const resolveStop ='Case selected for QA';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.resolveStop);
            await cwEventActionsPage.chooseResolveStop(resolveStop);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Case selected for QA';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Change state';
            endState = 'Awaiting documentation';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.chooseNewState(endState);
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Generate grant preview';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.generateGrantPreview);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Ready to issue';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Find matches (Issue grant)';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.findMatchesIssueGrant);
            await cwEventActionsPage.selectCaseMatches(caseRef, nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Case Matching (Issue grant)';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, caseMatchesTabConfig, caseMatchesConfig);

            nextStepName = 'Change DOB';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterNewDob(newConfig.newDob);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, deceasedTabConfigUpdated, createGrantOfProbateConfig);
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Issue grant';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.issueGrant);
            await cwEventActionsPage.issueGrant(caseRef);
            endState = 'Grant issued';
            await basePage.logInfo(scenarioName, endState, caseRef);

            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);

            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // When sending an email notification, the Date added for the email notification is set to today
            issueGrantConfig.date = dateFns.format(new Date(), testConfig.dateFormat);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, grantNotificationsTabConfig, issueGrantConfig);

            nextStepName = 'Post Grant Issue';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.postGrantIssue);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Post grant issued';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Resolve Post Grant Issue';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepConfig.resolvePostGrantIssue);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Grant issued';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            issueGrantConfig.date = dateFns.format(new Date(), testConfig.dateFormat);
            // TODO: Expects 6-7 arguments
            await basePage.seeCaseDetails(testInfo, caseRef, grantNotificationsTabConfig, issueGrantConfig);
        });
});
