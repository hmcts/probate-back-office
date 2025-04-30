// @ts-check
const {test} = require('../Fixtures/fixtures');

const dateFns = require('date-fns');

// const testConfig = require('src/test/config');
const createCaseConfig = require('../Pages/createCase/createCaseConfig.json');
const eventSummaryConfig = require('../Pages/eventSummary/eventSummaryConfig');

const createWillLodgementConfig = require('../Pages/createWillLodgement/createWillLodgementConfig');
const documentUploadConfig = require('../Pages/documentUpload/willLodgement/documentUploadConfig');
const generateDepositReceiptConfig = require('../Pages/generateDepositReceipt/generateDepositReceiptConfig');
const caseMatchesConfig = require('../Pages/caseMatches/willLodgement/caseMatchesConfig');
const withdrawWillConfig = require('../Pages/withdrawal/willLodgement/withdrawalConfig');

const historyTabConfig = require('../Pages/caseDetails/willLodgement/historyTabConfig');

const caseDetailsTabConfig = require('../Pages/caseDetails/willLodgement/caseDetailsTabConfig');
const testatorTabConfig = require('../Pages/caseDetails/willLodgement/testatorTabConfig');
const executorTabConfig = require('../Pages/caseDetails/willLodgement/executorTabConfig');

const caseDetailsTabUpdateConfig = require('../Pages/caseDetails/willLodgement/caseDetailsTabUpdateConfig');
const testatorTabUpdateConfig = require('../Pages/caseDetails/willLodgement/testatorTabUpdateConfig');
const executorTabUpdateConfig = require('../Pages/caseDetails/willLodgement/executorTabUpdateConfig');

const documentsTabUploadDocumentConfig = require('../Pages/caseDetails/willLodgement/documentsTabUploadDocumentConfig');
const documentsTabGenerateDepositReceiptConfig = require('../Pages/caseDetails/willLodgement/documentsTabGenerateDepositReceiptConfig');

const caseMatchesTabConfig = require('../Pages/caseDetails/willLodgement/caseMatchesTabConfig');
const willWithdrawalDetailsTabConfig = require('../Pages/caseDetails/willLodgement/willWithdrawalDetailsTabConfig');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

test.describe('Caseworker Will Lodgement - Withdraw will', () => {
    test('Caseworker Will Lodgement - Withdraw will',
        async ({basePage, signInPage, createCasePage, cwEventActionsPage}) => {
            const scenarioName = 'Caseworker Will Lodgement - Withdraw will';

            // BO Will Lodgement (Personal): Create a will lodgement -> Withdraw will

            // get unique suffix for names - in order to match only against 1 case
            const unique_deceased_user = Date.now();

            await basePage.logInfo(scenarioName, 'Login as Caseworker');
            await signInPage.authenticateWithIdamIfAvailable(false);

            // FIRST case is only needed for case-matching with SECOND one

            let nextStepName = 'Create a will lodgement';
            await basePage.logInfo(scenarioName, nextStepName);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_will, createCaseConfig.list3_text_will);
            await createCasePage.enterWillLodgementPage1('create');
            await createCasePage.enterWillLodgementPage2('create', unique_deceased_user);
            await createCasePage.enterWillLodgementPage3('create');
            await createCasePage.checkMyAnswers(nextStepName, 'Save and continue');

            // SECOND case - the main test case

            await basePage.logInfo(scenarioName, nextStepName);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_will, createCaseConfig.list3_text_will);
            await createCasePage.enterWillLodgementPage1('create');
            await createCasePage.enterWillLodgementPage2('create', unique_deceased_user);
            await createCasePage.enterWillLodgementPage3('create');
            await createCasePage.checkMyAnswers(nextStepName, 'Save and continue');
            let endState = 'Will lodgement created';

            const caseRef = await basePage.getCaseRefFromUrl();

            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, caseDetailsTabConfig, createWillLodgementConfig);
            await basePage.seeCaseDetails(caseRef, testatorTabConfig, createWillLodgementConfig);
            await basePage.seeCaseDetails(caseRef, executorTabConfig, createWillLodgementConfig);

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
            await cwEventActionsPage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);

            nextStepName = 'Amend will lodgement';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await createCasePage.enterWillLodgementPage1('update');
            await createCasePage.enterWillLodgementPage2('update', unique_deceased_user);
            await createCasePage.enterWillLodgementPage3('update');
            await createCasePage.checkMyAnswers(nextStepName);
            // Note that End State does not change when amending a Will Lodgement.
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, caseDetailsTabUpdateConfig, createWillLodgementConfig);
            await basePage.seeCaseDetails(caseRef, testatorTabUpdateConfig, createWillLodgementConfig);
            await basePage.seeCaseDetails(caseRef, executorTabUpdateConfig, createWillLodgementConfig);

            nextStepName = 'Generate deposit receipt';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            // Note that End State does not change when generating a deposit receipt.
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            // When generating a deposit receipt, the Date added for the deposit receipt document is set to today
            generateDepositReceiptConfig.dateAdded = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
            await basePage.seeCaseDetails(caseRef, documentsTabGenerateDepositReceiptConfig, generateDepositReceiptConfig);

            // "reverting" update back to defaults - to enable case-match with matching case
            nextStepName = 'Amend will lodgement';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await createCasePage.enterWillLodgementPage2('update2orig');
            await createCasePage.checkMyAnswers(nextStepName);

            nextStepName = 'Match application';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.selectCaseMatches(caseRef, nextStepName);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Will lodged';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

            nextStepName = 'Withdraw will';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await cwEventActionsPage.selectWithdrawalReason(caseRef, withdrawWillConfig);
            await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
            endState = 'Will withdrawn';
            await basePage.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
            await basePage.seeCaseDetails(caseRef, willWithdrawalDetailsTabConfig, withdrawWillConfig);
            await basePage.logInfo(scenarioName, endState, caseRef);

            await signInPage.signOut();

        });
});
