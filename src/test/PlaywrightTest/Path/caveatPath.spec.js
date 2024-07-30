const {test,expect} = require('../Fixtures/fixtures');
const dateFns = require('date-fns');

const createCaseConfig = require('../Pages/createCase/createCaseConfig.json');
const eventSummaryConfig = require('../Pages/eventSummary/eventSummaryConfig.json');

const createCaveatConfig = require('../Pages/createCaveat/createCaveatConfig.json');
const emailCaveatorConfig = require('../Pages/emailNotifications/caveat/emailCaveatorConfig.json');
const caseMatchesConfig = require('../Pages/caseMatches/caveat/caseMatchesConfig.json');
const caseMatchesTabConfig = require('../Pages/caseDetails/caveat/caseMatchesTabConfig');

const documentUploadConfig = require('../Pages/documentUpload/caveat/documentUploadConfig');

const historyTabConfig = require('../Pages/caseDetails/caveat/historyTabConfig');

const caseDetailsTabConfig = require('../Pages/caseDetails/caveat/caseDetailsTabConfig');
const deceasedDetailsTabConfig = require('../Pages/caseDetails/caveat/deceasedDetailsTabConfig');
const caveatorDetailsTabConfig = require('../Pages/caseDetails/caveat/caveatorDetailsTabConfig');
const caveatDetailsTabConfig = require('../Pages/caseDetails/caveat/caveatDetailsTabConfig');

const caseDetailsTabUpdateConfig = require('../Pages/caseDetails/caveat/caseDetailsTabUpdateConfig');
const deceasedDetailsTabUpdateConfig = require('../Pages/caseDetails/caveat/deceasedDetailsTabUpdateConfig');
const caveatorDetailsTabUpdateConfig = require('../Pages/caseDetails/caveat/caveatorDetailsTabUpdateConfig');
const caveatDetailsTabUpdateConfig = require('../Pages/caseDetails/caveat/caveatDetailsTabUpdateConfig');

const documentsTabEmailCaveatorConfig = require('../Pages/caseDetails/caveat/documentsTabEmailCaveatorConfig');
const documentsTabUploadDocumentConfig = require('../Pages/caseDetails/caveat/documentsTabUploadDocumentConfig');

const registrarsDecisionConfig = require('../Pages/caseDetails/caveat/registrarsDecisionConfig');
const registrarsDecisionTabConfig = require('../Pages/caseDetails/caveat/registrarsDecisionTabConfig');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

test.describe('Caseworker Caveat1 - Order summons', () => {
    test('Caseworker Caveat1 - Order summons',
        async ({basePage, signInPage, createCasePage, cwEventActionsPage, makeAxeBuilder}, testInfo) => {
            const scenarioName = 'Caseworker Caveat1 - Order summons';
            // BO Caveat (Personal): Raise a caveat -> Caveat not matched -> Order summons

            // get unique suffix for names - in order to match only against 1 case
            const unique_deceased_user = Date.now();

            await basePage.logInfo(scenarioName, 'Login as Caseworker');
            await signInPage.authenticateWithIdamIfAvailable(false);

            // FIRST case is only needed for case-matching with SECOND one

            let nextStepName = 'Raise a caveat';
            await basePage.logInfo(scenarioName, nextStepName);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
            await createCasePage.enterCaveatPage1('create');
            await createCasePage.enterCaveatPage2('create', unique_deceased_user);
            await createCasePage.enterCaveatPage3('create');
            await createCasePage.enterCaveatPage4('create');
            await createCasePage.checkMyAnswers(nextStepName);

            // SECOND case - the main test case

            await basePage.logInfo(scenarioName, nextStepName);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
            await createCasePage.enterCaveatPage1('create');
            await createCasePage.enterCaveatPage2('create', unique_deceased_user);
            await createCasePage.enterCaveatPage3('create');
            await createCasePage.enterCaveatPage4('create');
            await createCasePage.checkMyAnswers(nextStepName);
            let endState = 'Caveat raised';

            const caseRef = await basePage.getCaseRefFromUrl();

            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, caseDetailsTabConfig, createCaveatConfig);
            await basePage.seeCaseDetails(caseRef, deceasedDetailsTabConfig, createCaveatConfig);
            await basePage.seeCaseDetails(caseRef, caveatorDetailsTabConfig, createCaveatConfig);

            // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
            createCaveatConfig.caveat_expiry_date = dateFns.format(legacyParse(dateFns.addMonths(new Date(), 6)), convertTokens('D MMM YYYY'));
            await basePage.seeCaseDetails(caseRef, caveatDetailsTabConfig, createCaveatConfig);

            nextStepName = 'Registrar\'s decision';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.registrarsDecision(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            await basePage.seeCaseDetails(caseRef, registrarsDecisionTabConfig, registrarsDecisionConfig);

            nextStepName = 'Email caveator'; // When in state 'Caveat raised'
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.emailCaveator(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // Note that End State does not change when emailing the caveator.
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // When emailing the caveator, the Date added for the email document is set to today
            emailCaveatorConfig.dateAdded = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
            await basePage.seeCaseDetails(caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);

            nextStepName = 'Caveat match';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.selectCaseMatchesForCaveat(caseRef, nextStepName, true, caseMatchesConfig.addNewButton);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Caveat matching';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

            nextStepName = 'Email caveator'; // When in state 'Caveat closed'
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.emailCaveator(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // Note that End State does not change when emailing the caveator.
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // When emailing the caveator, the Date added for the email document is set to today
            emailCaveatorConfig.dateAdded = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
            await basePage.seeCaseDetails(caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);

            nextStepName = 'Caveat not matched';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Caveat not matched';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Upload document';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.uploadDocument(caseRef, documentUploadConfig);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // Note that End State does not change when uploading a document.
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, documentsTabUploadDocumentConfig, documentUploadConfig);

            nextStepName = 'Add comment';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // Note that End State does not change when adding a comment.
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Await caveat resolution';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Awaiting caveat resolution';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Warning requested';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Warning validation';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Issue caveat warning';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Awaiting warning response';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Order summons';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Summons ordered';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Amend caveat details';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await createCasePage.enterCaveatPage1('update');
            await createCasePage.enterCaveatPage2('update', unique_deceased_user);
            await createCasePage.enterCaveatPage3('update');
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);

            // Note that End State does not change when amending the caveat details.
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, caseDetailsTabUpdateConfig, createCaveatConfig);
            await basePage.seeCaseDetails(caseRef, deceasedDetailsTabUpdateConfig, createCaveatConfig);
            await basePage.seeCaseDetails(caseRef, caveatorDetailsTabUpdateConfig, createCaveatConfig);
            await basePage.seeCaseDetails(caseRef, caveatDetailsTabUpdateConfig, createCaveatConfig);

            nextStepName = 'Withdraw caveat';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.withdrawCaveatPage1();
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Caveat closed';
            await basePage.logInfo(scenarioName, endState);
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            await signInPage.signOut();

        });
});
