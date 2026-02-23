// @ts-check
const {test} = require('../../Fixtures/fixtures');
const dateFns = require('date-fns');

const createCaseConfig = require('../../Pages/createCase/createCaseConfig.json');
const eventSummaryConfig = require('../../Pages/eventSummary/eventSummaryConfig.json');

const createCaveatConfig = require('../../Pages/createCaveat/createCaveatConfig.json');
const emailCaveatorConfig = require('../../Pages/emailNotifications/caveat/emailCaveatorConfig.json');
const reopenCaveatConfig = require('../../Pages/reopenningCases/caveat/reopenCaveatConfig.json');
const caseMatchesConfig = require('../../Pages/caseMatches/caveat/caseMatchesConfig.json');
const documentUploadConfig = require('../../Pages/documentUpload/caveat/documentUploadConfig.json');

const historyTabConfig = require('../../Pages/caseDetails/caveat/historyTabConfig.json');

const caseDetailsTabConfig = require('../../Pages/caseDetails/caveat/caseDetailsTabConfig.json');
const deceasedDetailsTabConfig = require('../../Pages/caseDetails/caveat/deceasedDetailsTabConfig.json');
const caveatorDetailsTabConfig = require('../../Pages/caseDetails/caveat/caveatorDetailsTabConfig.json');
const caveatDetailsTabConfig = require('../../Pages/caseDetails/caveat/caveatDetailsTabConfig.json');
const caveatDetailsTabReopenConfig = require('../../Pages/caseDetails/caveat/caveatDetailsTabReopenConfig.json');

const documentsTabEmailCaveatorConfig = require('../../Pages/caseDetails/caveat/documentsTabEmailCaveatorConfig.json');
// this check has been removed as a temporary measure 14/01/2020, due to an Elastic Search bug
// const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/caveat/caseMatchesTabConfig');
const documentsTabUploadDocumentConfig = require('../../Pages/caseDetails/caveat/documentsTabUploadDocumentConfig.json');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

test.describe('Caseworker Caveat3 - Caveat expired', () => {
    test('Caseworker Caveat3 - Caveat expired',
        async ({basePage, signInPage, createCasePage, cwEventActionsPage}, testInfo) => {
            const scenarioName = 'Caseworker Caveat3 - Caveat expired';

            // BO Caveat (Personal): Raise a caveat -> Caveat not matched -> Caveat expired
            // Test File
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
            // await basePage.logInfo(endState);

            const caseRef = await basePage.getCaseRefFromUrl();
            // await basePage.logInfo(caseRef);
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabConfig, createCaveatConfig);
            await basePage.seeCaseDetails(testInfo, caseRef, deceasedDetailsTabConfig, createCaveatConfig);
            await basePage.seeCaseDetails(testInfo, caseRef, caveatorDetailsTabConfig, createCaveatConfig);
            // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
            createCaveatConfig.caveat_expiry_date = dateFns.format(legacyParse(dateFns.addMonths(new Date(), 6)), convertTokens('D MMM YYYY'));
            await basePage.seeCaseDetails(testInfo, caseRef, caveatDetailsTabConfig, createCaveatConfig);

            nextStepName = 'Caveat match';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.selectCaseMatches(caseRef, nextStepName, true, caseMatchesConfig.addNewButton);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Caveat matching';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // this check has been removed as a temporary measure 14/01/2020, due to an Elastic Search bug
            // await I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

            nextStepName = 'Await caveat resolution';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Awaiting caveat resolution';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Upload document';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.uploadDocument(caseRef, documentUploadConfig);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // Note that End State does not change when uploading a document.
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(testInfo, caseRef, documentsTabUploadDocumentConfig, documentUploadConfig);

            nextStepName = 'Add comment';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // Note that End State does not change when adding a comment.
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Caveat expired';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Caveat closed';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Email caveator'; // When in state 'Caveat closed'
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.emailCaveator(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // Note that End State does not change when emailing the caveator.
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // When emailing the caveator, the Date added for the email document is set to today
            emailCaveatorConfig.dateAdded = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
            await basePage.seeCaseDetails(testInfo, caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);

            nextStepName = 'Reopen caveat'; // When in state 'Caveat closed'
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.reopenCaveat(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Caveat raised';
            await basePage.logInfo(scenarioName, endState);
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(testInfo, caseRef, caveatDetailsTabReopenConfig, reopenCaveatConfig);

            nextStepName = 'Withdraw caveat';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.withdrawCaveatPage1();
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Caveat closed';
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Email caveator'; // When in state 'Caveat closed'
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.emailCaveator(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // Note that End State does not change when emailing the caveator.
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // When emailing the caveator, the Date added for the email document is set to today
            emailCaveatorConfig.dateAdded = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
            await basePage.seeCaseDetails(testInfo, caseRef, documentsTabEmailCaveatorConfig, emailCaveatorConfig);

            nextStepName = 'Reopen caveat'; // When in state 'Caveat closed'
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.reopenCaveat(caseRef);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Caveat raised';
            await basePage.logInfo(scenarioName, endState);
            await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(testInfo, caseRef, caveatDetailsTabReopenConfig, reopenCaveatConfig);

            await signInPage.signOut();
        });
});
