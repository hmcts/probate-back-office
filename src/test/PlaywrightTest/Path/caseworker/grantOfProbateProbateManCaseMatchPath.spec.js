// @ts-check
const {test} = require('../../Fixtures/fixtures');

const {testConfig} = require ('../../Configs/config');
const createCaseConfig = require('../../Pages/createCase/createCaseConfig.json');

const caseMatchesConfig = require('../../Pages/caseMatches/grantOfProbate/probateManCaseMatchesConfig.json');
const createGrantOfProbateManualProbateManCaseConfig = require('../../Pages/createGrantOfProbateManualForProbateMan/createGrantOfProbateManualProbateManCaseConfig.json');
const eventSummaryConfig = require('../../Pages/eventSummary/eventSummaryConfig.json');
const applicantDetailsTabConfig = require('../../Pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE.json');
const caseDetailsTabConfig = require('../../Pages/caseDetails/grantOfProbate/caseDetailsTabConfigProbateMan.json');

const caseMatchesTabConfig = require('../../Pages/caseDetails/grantOfProbate/probateManCaseMatchesTabConfig.json');
const deceasedTabConfig = require('../../Pages/caseDetails/grantOfProbate/deceasedTabConfigEE.json');
const historyTabConfig = require('../../Pages/caseDetails/grantOfProbate/historyTabConfig.json');
const copiesTabConfig = require('../../Pages/caseDetails/grantOfProbate/copiesTabConfig.json');
const caseProgressConfig = require('../../Pages/caseProgressStandard/caseProgressConfig.json');

test.describe('Caseworker Grant of Representation - probateman case match', () => {
    test('Caseworker Grant of Representation - probateman case match',
        async ({basePage, signInPage, createCasePage, cwEventActionsPage}, testInfo) => {
            const scenarioName = 'Caseworker Grant of Representation - probateman case match';
            if (testConfig.TestBackOfficeUrl.includes('demo') || testConfig.TestBackOfficeUrl.includes('aat')) {
                let endState;
                await basePage.logInfo(scenarioName, 'Login as Caseworker');
                await signInPage.authenticateWithIdamIfAvailable(false);

                // Create case with same deceased details from legacy database

                let nextStepName = 'PA1P/PA1A/Solicitors Manual';
                await basePage.logInfo(scenarioName, nextStepName + ' - first case');
                await createCasePage.selectNewCase();
                await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor_manual);
                await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualForProbateManPage1');
                await createCasePage.enterGrantOfProbateManualPage1('create', createGrantOfProbateManualProbateManCaseConfig);
                await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualForProbateManPage2');
                await createCasePage.enterGrantOfProbateManualPage2('createIHT400');
                await createCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
                await basePage.logInfo(scenarioName, 'enterGrantOfProbateManualForProbateManPage3');
                await createCasePage.enterGrantOfProbateManualPage3('create', createGrantOfProbateManualProbateManCaseConfig);
                await createCasePage.checkMyAnswers(nextStepName);
                endState = 'Awaiting documentation';

                const caseRef = await basePage.getCaseRefFromUrl();

                await basePage.logInfo(scenarioName, nextStepName, caseRef);
                await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
                await basePage.seeCaseDetails(testInfo, caseRef, deceasedTabConfig, createGrantOfProbateManualProbateManCaseConfig);
                await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabConfig, createGrantOfProbateManualProbateManCaseConfig);
                await basePage.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
                await basePage.seeCaseDetails(testInfo, caseRef, applicantDetailsTabConfig, createGrantOfProbateManualProbateManCaseConfig);
                await basePage.seeCaseDetails(testInfo, caseRef, copiesTabConfig, createGrantOfProbateManualProbateManCaseConfig);

                nextStepName = 'Find matches (cases)';
                await basePage.logInfo(scenarioName, nextStepName, caseRef);
                await cwEventActionsPage.chooseNextStep(nextStepName);
                await cwEventActionsPage.selectProbateManCaseMatchesForGrantOfProbate(caseRef, nextStepName, false);
                await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
                endState = 'Awaiting documentation';
                await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
                await basePage.seeCaseDetails(testInfo, caseRef, caseMatchesTabConfig, caseMatchesConfig);
                await cwEventActionsPage.verifyProbateManCcdCaseNumber();
            }
        });
});
