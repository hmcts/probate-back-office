'use strict';

// This test is in the caseworker folder, as although it alternates between caseworker
// and solicitor (prof user), the test is to be run on the CCD ui, which the caseworker forlder is actually for
const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStandard/caseProgressConfig');
const serviceRequestTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig');
const serviceRequestReviewTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig');
const documentUploadSolTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadSolTabConfigBilingual');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Case Progress - Reenter Deceased Details';
Scenario(scenarioName, async function ({I}) {
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
        await I.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
        await I.provideIhtValues(caseProgressConfig.IHTGross, caseProgressConfig.IHTNet, 'IHT400');
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
            {locator: {css: '#willHasCodicils_No'}},
            {locator: {css: '#languagePreferenceWelsh_Yes'}}
        ]);

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

        await I.logInfo(scenarioName, 'Reenter solicitor details');
        await I.caseProgressClickElementsAndContinue([{css: '#solsSOTNeedToUpdate_Yes'}]);
        await I.caseProgressClickSelectOrFillElementsAndContinue([{locator: {css: '#solsAmendLegalStatmentSelect'}, option: '1: SolAppCreatedSolicitorDtls'}]);
        await I.caseProgressContinueWithoutChangingAnything();
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 0,
            numInProgress: 4,
            numNotStarted: 0,
            linkText: 'Add Probate practitioner details',
            linkUrl: '/trigger/solicitorUpdateAppSolDtls/solicitorUpdateAppSolDtlssolicitorUpdateAppSolDtlsPage1',
            goToNextStep: true});

        await I.caseProgressContinueWithoutChangingAnything(3);

        await I.caseProgressSelectPenultimateNextStepAndGo();
        await I.caseProgressContinueWithoutChangingAnything(7);

        await I.caseProgressSelectPenultimateNextStepAndGo();
        await I.caseProgressContinueWithoutChangingAnything(6);

        await I.caseProgressSelectPenultimateNextStepAndGo();

        await I.logInfo(scenarioName, 'Reenter deceased details');
        await I.caseProgressClickElementsAndContinue([{css: '#solsSOTNeedToUpdate_Yes'}]);
        await I.caseProgressClickSelectOrFillElementsAndContinue([{locator: {css: '#solsAmendLegalStatmentSelect'}, option: '2: SolAppCreatedDeceasedDtls'}]);
        await I.caseProgressContinueWithoutChangingAnything();
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 1,
            numInProgress: 3,
            numNotStarted: 0,
            linkText: 'Add deceased details',
            linkUrl: '/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1',
            goToNextStep: true});

        await I.caseProgressContinueWithoutChangingAnything(7);

        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 2,
            numInProgress: 2,
            numNotStarted: 0,
            linkText: 'Add application details',
            linkUrl: '/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1',
            goToNextStep: true});

        await I.caseProgressContinueWithoutChangingAnything(6);

        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 3,
            numInProgress: 1,
            numNotStarted: 0,
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

        await I.logInfo(scenarioName, 'Submit confirmation');
        await I.completeApplicationPage6();
        await I.completeApplicationPage7();
        await I.caseProgressSubmittedConfirmation();

        const caseRef = await I.caseProgressCheckCaseProgressTab({
            numCompleted: 4,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Make payment',
            linkUrl: '#Service%20Request'});

        await I.logInfo(scenarioName, 'Payment');
        await I.makePaymentPage1(caseRef, serviceRequestTabConfig);
        await I.reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig);
        await I.makePaymentPage2(caseRef);
        await I.viewPaymentStatus(caseRef);

        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 5,
            numInProgress: 1,
            numNotStarted: 0,
            signOut: true});

        await I.logInfo(scenarioName, 'Select for QA', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(createCaseConfig.list2_text_gor, caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Select for QA');
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Select for QA', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(createCaseConfig.list2_text_gor, caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 7,
            numInProgress: 1,
            numNotStarted: 0,
            signOut: true});

        await I.logInfo(scenarioName, 'Generate grant preview', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(createCaseConfig.list2_text_gor, caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Generate grant preview');
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Generate grant preview', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(createCaseConfig.list2_text_gor, caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 7,
            numInProgress: 1,
            numNotStarted: 0,
            signOut: true});

        await I.logInfo(scenarioName, 'Find matches (Issue grant)', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(createCaseConfig.list2_text_gor, caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Find matches (Issue grant)');
        await I.selectCaseMatchesForGrantOfProbate(caseRef, 'Find matches (Issue grant)', false, null, true);

        await I.signOut();

        await I.logInfo(scenarioName, 'Check progress tab for Case Matching (Issue grant)', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(createCaseConfig.list2_text_gor, caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 8,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'Issue grant', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(createCaseConfig.list2_text_gor, caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Issue grant');
        await I.caseProgressClickElementsAndContinue([{css: '#boSendToBulkPrint_No'}]);
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Issue grant', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(createCaseConfig.list2_text_gor, caseRef);

        await I.seeTabDetailsBilingual(caseRef, documentUploadSolTabConfig, caseProgressConfig);
        await I.clickTab('Case Progress');

        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 9,
            numInProgress: 0,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'scenario complete', caseRef);

    } catch (e) {
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }
}).retry(testConfig.TestRetryScenarios);
