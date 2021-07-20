'use strict';

// This test is in the caseworker folder, as although it alternates between caseworker
// and solicitor (prof user), the test is to be run on the CCD ui, which the caseworker forlder is actually for
const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStandard/caseProgressConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Case Progress - standard path';

Scenario('03 BO Case Progress E2E - standard path', async function ({I}) {
    /* eslint-disable no-console */
    try {

        const unique_deceased_user = Date.now();
        await I.logInfo(scenarioName, 'Login as Solicitor');

        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
        await I.waitForNavigationToComplete(commonConfig.continueButton, testConfig.CreateCaseContinueDelay);

        await I.logInfo(scenarioName, 'Initial application entry');
        await I.caseProgressSolicitorDetails(caseProgressConfig);
        await I.caseProgressSolicitorDetailsCheckAnswers(caseProgressConfig);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 1,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Add deceased details',
            linkUrl: '/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1',
            goToNextStep: true});

        await I.logInfo(scenarioName, 'Deceased details');
        await I.caseProgressDeceasedDetails(caseProgressConfig, unique_deceased_user);
        await I.caseProgressDeceasedDetails2(caseProgressConfig, unique_deceased_user);
        await I.caseProgressClickElementsAndContinue([{css: '#solsWillType-WillLeft'}]);
        await I.caseProgressClickElementsAndContinue([{css: '#willDispose_Yes'}, {css: '#englishWill_Yes'}, {css: '#appointExec_Yes'}]);
        await I.caseProgressStandardDeceasedDetailsCheck(unique_deceased_user);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 2,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Add application details',
            linkUrl: '/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1',
            goToNextStep: true});

        await I.logInfo(scenarioName, 'Add application details');
        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#willAccessOriginal_Yes'}},
            {locator: {css: '#originalWillSignedDate-day'}, text: '10'},
            {locator: {css: '#originalWillSignedDate-month'}, text: '10'},
            {locator: {css: '#originalWillSignedDate-year'}, text: '2018'},
            {locator: {css: '#willHasCodicils_No'}}]);

        await I.logInfo(scenarioName, 'Dispense with notice and clearing type');
        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#dispenseWithNotice_No'}},
            {locator: {css: '#titleAndClearingType-TCTNoT'}},
        ]);

        await I.logInfo(scenarioName, 'Remaining application details');
        await I.caseProgressClickElementsAndContinue([{css: '#otherExecutorExists_No'}]);

        await I.caseProgressWaitForElementThenContinue('#furtherEvidenceForApplication');
        await I.caseProgressWaitForElementThenContinue('#solsAdditionalInfo');

        // More extensive checks already performed at this stage for stop/escalate issue
        await I.caseProgressCheckYourAnswers();
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 3,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Review and sign legal statement and submit application',
            linkUrl: '/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1',
            goToNextStep: true});

        await I.logInfo(scenarioName, 'Confirm application');
        await I.caseProgressClickElementsAndContinue([{css: '#solsSOTNeedToUpdate_No'}]);
        await I.caseProgressWaitForElementThenContinue('#solsLegalStatementUpload');

        await I.caseProgressClickElementsAndContinue([{css: '#solsReviewSOTConfirmCheckbox1-BelieveTrue'},
            {css: '#solsReviewSOTConfirmCheckbox2-BelieveTrue'}]);

        // extra copies
        await I.caseProgressWaitForElementThenContinue('#extraCopiesOfGrant');

        await I.logInfo(scenarioName, 'Payment');
        await I.caseProgressFeePayment(caseProgressConfig);
        await I.caseProgressCompleteApplication();

        await I.logInfo(scenarioName, 'Submit confirmation');
        await I.caseProgressSubmittedConfirmation();

        const caseRef = await I.caseProgressCheckCaseProgressTab({
            numCompleted: 4,
            numInProgress: 1,
            numNotStarted: 0,
            signOut: true});

        await I.logInfo(scenarioName, 'Print case', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Print the case');
        await I.caseProgressClickSelectOrFillElementsAndContinue([{locator: {css: '#casePrinted'}, option: '1: Yes'}]);
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Print case', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 4,
            numInProgress: 1,
            numNotStarted: 0,
            signOut: true});

        await I.logInfo(scenarioName, 'Mark as ready for examination', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Mark as ready for examination');
        await I.caseProgressClickElementsAndContinue([{css: '#boEmailDocsReceivedNotification_No'}]);
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Mark as ready for examination', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 6,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'Find matches (Examining)', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Find matches (Examining)');
        await I.selectCaseMatchesForGrantOfProbate(caseRef, 'Find matches (Examining)', false, null, true);
        await I.signOut();

        await I.logInfo(scenarioName, 'Check progress tab for Find matches (Examining)', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 6,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'Examine case', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Examine case');
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Examine case');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 6,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'Mark as ready to issue', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Mark as ready to issue');
        await I.caseProgressClickElementsAndContinue([
            {css: '#boExaminationChecklistQ1_Yes'},
            {css: '#boExaminationChecklistQ2_Yes'},
            {css: '#boExaminationChecklistRequestQA_No'}]);
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Mark as ready to issue', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 6,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'Find matches (Issue grant)', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.navigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Find matches (Issue grant)');
        await I.selectCaseMatchesForGrantOfProbate(caseRef, 'Find matches (Issue grant)', false, null, true);

        await I.signOut();

        await I.logInfo(scenarioName, 'Check progress tab for Case Matching (Issue grant)');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.navigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 7,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'Issue grant');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.navigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Issue grant');
        await I.caseProgressClickElementsAndContinue([{css: '#boSendToBulkPrint-No'}]);
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Issue grant');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 8,
            numInProgress: 0,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'Scenario complete', caseRef);

    } catch (e) {
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }
}).retry(testConfig.TestRetryScenarios);
