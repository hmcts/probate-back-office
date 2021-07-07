'use strict';

// This test is in the caseworker folder, as although it alternates between caseworker
// and solicitor (prof user), the test is to be run on the CCD ui, which the caseworker forlder is actually for
const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStandard/caseProgressConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('03 BO Case Progress E2E - standard path', async function ({I}) {
    /* eslint-disable no-console */
    try {
        // IDAM
        await I.authenticateWithIdamIfAvailable(true);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor, 0);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        console.info('Initial application entry');
        await I.caseProgressSolicitorDetails(caseProgressConfig);
        await I.caseProgressSolicitorDetailsCheckAnswers(caseProgressConfig);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 1,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Add deceased details',
            linkUrl: '/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1',
            goToNextStep: true});

        console.info('Deceased details');
        await I.caseProgressDeceasedDetails(caseProgressConfig);
        await I.caseProgressDeceasedDetails2(caseProgressConfig);
        await I.caseProgressClickElementsAndContinue([{css: '#solsWillType-WillLeft'}]);
        await I.caseProgressClickElementsAndContinue([{css: '#willDispose_Yes'}, {css: '#englishWill_Yes'}, {css: '#appointExec_Yes'}]);
        await I.caseProgressStandardDeceasedDetailsCheck();
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 2,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Add application details',
            linkUrl: '/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1',
            goToNextStep: true});

        console.info('Add application details');
        await I.caseProgressClickElementsAndContinue([{css: '#willAccessOriginal_Yes'}, {css: '#willHasCodicils_No'}]);
        await I.caseProgressClickElementsAndContinue([{css: '#otherExecutorExists_No'}]);
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

        console.info('Confirm application');
        await I.caseProgressClickElementsAndContinue([{css: '#solsSOTNeedToUpdate_No'}]);
        await I.caseProgressConfirmApplication();

        await I.caseProgressClickSelectOrFillElementsAndContinue([{locator: {css: '#solsSOTJobTitle'}, text: caseProgressConfig.JobTitle}]);
        await I.caseProgressCompleteApplication();

        console.info('Payment');
        await I.caseProgressFeePayment(caseProgressConfig);
        await I.caseProgressCompleteApplication();

        console.info('Submit confirmation');
        await I.caseProgressSubmittedConfirmation();

        const caseRef = await I.caseProgressCheckCaseProgressTab({
            numCompleted: 4,
            numInProgress: 1,
            numNotStarted: 0,
            signOut: true});

        console.info('Print case');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressNavigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChangeState('Print the case');
        await I.caseProgressClickSelectOrFillElementsAndContinue([{locator: {css: '#casePrinted'}, option: '1: Yes'}]);
        await I.caseProgressClickGoAndSignOut();

        console.info('Check progress tab for Print case');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 4,
            numInProgress: 1,
            numNotStarted: 0,
            signOut: true});

        console.info('Mark as ready for examination');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressNavigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChangeState('Mark as ready for examination');
        await I.caseProgressClickElementsAndContinue([{css: '#boEmailDocsReceivedNotification_No'}]);
        await I.caseProgressClickGoAndSignOut();

        console.info('Check progress tab for Mark as ready for examination');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 6,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        console.info('Find matches (Examining)');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressNavigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChangeState('Find matches (Examining)');
        await I.selectCaseMatchesForGrantOfProbate(caseRef, 'Find matches (Examining)', false, null, true);
        await I.waitForElement({css: '#sign-out'});
        await I.waitForNavigationToComplete('#sign-out');

        console.info('Check progress tab for Find matches (Examining)');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 6,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        console.info('Examine case');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressNavigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChangeState('Examine case');
        await I.caseProgressClickGoAndSignOut();

        console.info('Check progress tab for Examine case');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 6,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        console.info('Mark as ready to issue');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressNavigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChangeState('Mark as ready to issue');
        await I.caseProgressClickElementsAndContinue([
            {css: '#boExaminationChecklistQ1_Yes'},
            {css: '#boExaminationChecklistQ2_Yes'},
            {css: '#boExaminationChecklistRequestQA_No'}]);
        await I.caseProgressClickGoAndSignOut();

        console.info('Check progress tab for Mark as ready to issue');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 6,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        console.info('Find matches (Issue grant)');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressNavigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChangeState('Find matches (Issue grant)');
        await I.selectCaseMatchesForGrantOfProbate(caseRef, 'Find matches (Issue grant)', false, null, true);

        await I.waitForVisible({css: '#sign-out'});
        await I.waitForNavigationToComplete('#sign-out');

        console.info('Check progress tab for Case Matching (Issue grant)');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 7,
            numInProgress: 1,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        console.info('Issue grant');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressNavigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChangeState('Issue grant');
        await I.caseProgressClickElementsAndContinue([{css: '#boSendToBulkPrint_No'}]);
        await I.caseProgressClickGoAndSignOut();

        console.info('Check progress tab for Issue grant');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 8,
            numInProgress: 0,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        console.info('03 BO Case Progress E2E - standard: complete');

    } catch (e) {
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }
}).retry(testConfig.TestRetryScenarios);
