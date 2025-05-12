'use strict';

// This test is in the caseworker folder, as although it alternates between caseworker
// and solicitor (prof user), the test is to be run on the CCD ui, which the caseworker forlder is actually for

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStopEscalateIssue/caseProgressConfig');
const solicitorDetailsHtmlCheck = require('src/test/end-to-end/pages/caseProgressStopEscalateIssue/solicitorDetailsHtmlCheck');
const solCheckAnswersHtmlCheck = require('src/test/end-to-end/pages/caseProgressStopEscalateIssue/solCheckAnswersHtmlCheck');
const serviceRequestTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig');
const serviceRequestReviewTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig');
const documentUploadSolTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadSolTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Case Progress - stop/escalate/issue';
Scenario('04 BO Case Progress E2E - stop/escalate/issue', async function ({I}) {
    try {
        const unique_deceased_user = Date.now();
        await I.logInfo(scenarioName, 'Login as Solicitor');

        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
        await I.waitForNavigationToComplete(commonConfig.continueButton, testConfig.CreateCaseContinueDelay);

        /* eslint-disable no-console */
        await I.logInfo(scenarioName, 'Initial application entry');
        await I.caseProgressSolicitorDetails(caseProgressConfig);
        await I.caseProgressSolicitorDetailsCheckAnswers(caseProgressConfig, solicitorDetailsHtmlCheck.htmlCheck);
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
        await I.caseProgressStopEscalateIssueDeceasedDetailsCheck(unique_deceased_user);
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
            {locator: {css: '#languagePreferenceWelsh_No'}}
        ]);

        await I.logInfo(scenarioName, 'Dispense with notice and clearing type');
        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#dispenseWithNotice_No'}},
            {locator: {css: '#titleAndClearingType-TCTNoT'}},
        ]);

        await I.logInfo(scenarioName, 'Remaining application details');
        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#primaryApplicantForenames'}, text: 'Fred'},
            {locator: {css: '#primaryApplicantSurname'}, text: 'Bassett'},
            {locator: {css: '#primaryApplicantHasAlias_No'}},
            {locator: {css: '#primaryApplicantIsApplying_Yes'}},
            {locator: {css: createCaseConfig.UKpostcodeLink}},
            {locator: {css: '#primaryApplicantAddress__detailAddressLine1'}, text: caseProgressConfig.solAddr1},
            {locator: {css: '#primaryApplicantAddress__detailPostTown'}, text: caseProgressConfig.solAddrTown},
            {locator: {css: '#primaryApplicantAddress__detailPostCode'}, text: caseProgressConfig.solAddrPostcode},
            {locator: {css: '#primaryApplicantAddress__detailCountry'}, text: caseProgressConfig.solAddrCountry},
            {locator: {css: '#otherExecutorExists_No'}}
        ]);

        await I.caseProgressWaitForElementThenContinue('#furtherEvidenceForApplication');
        await I.caseProgressWaitForElementThenContinue('#solsAdditionalInfo');
        await I.caseProgressCheckYourAnswers(solCheckAnswersHtmlCheck.htmlCheck);
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

        await I.logInfo(scenarioName, 'Stop case', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Stop case');
        await I.caseProgressStopEscalateIssueAddCaseStoppedReason();
        await I.caseProgressContinueWithoutChangingAnything();
        await I.signOut();

        await I.logInfo(scenarioName, 'Check progress tab for Case stopped', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressStopEscalateIssueStoppedTabCheck();

        await I.logInfo(scenarioName, 'Escalate case to registrar', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Escalate to registrar', caseRef);
        await I.caseProgressSelectEscalateReason();
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Case escalated', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressStopEscalateIssueEscalatedTabCheck();

        await I.logInfo(scenarioName, 'Stop case', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Stop case');
        await I.caseProgressStopEscalateIssueCaseStopAgainReason();
        await I.caseProgressContinueWithoutChangingAnything();
        await I.signOut();

        await I.logInfo(scenarioName, 'Check progress tab for Case stopped', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressStopEscalateIssueStoppedTabCheck();

        await I.logInfo(scenarioName, 'Resolve stop', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Resolve stop', caseRef);
        await I.caseProgressStopEscalateIssueAddCaseResolveStop();
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Resolve stop', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 5,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'Select for QA', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Select for QA');
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Select for QA', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 7,
            numInProgress: 1,
            numNotStarted: 0,
            signOut: true});

        await I.logInfo(scenarioName, 'Generate grant preview', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Generate grant preview');
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Generate grant preview', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 7,
            numInProgress: 1,
            numNotStarted: 0,
            signOut: true});

        await I.logInfo(scenarioName, 'Find matches (Issue grant)', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Find matches (Issue grant)');
        await I.selectCaseMatchesForGrantOfProbate(caseRef, 'Find matches (Issue grant)', false, null, true);
        await I.signOut();

        await I.logInfo(scenarioName, 'Check progress tab for Case Matching (Issue grant)', caseRef);
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 8,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        await I.logInfo(scenarioName, 'Issue grant', caseRef);
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);
        await I.caseProgressCaseworkerChooseNextStepAndGo('Issue grant');
        await I.caseProgressClickElementsAndContinue([{css: '#boSendToBulkPrint_No'}]);
        await I.caseProgressClickSubmitAndSignOut();

        await I.logInfo(scenarioName, 'Check progress tab for Issue grant', caseRef);
        // log back in as solicitor & check all sections completed
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.navigateToCase(caseRef);

        await I.seeTabDetails(caseRef, documentUploadSolTabConfig, caseProgressConfig);
        await I.clickTab('Case Progress');

        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 9,
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
