'use strict';

// This test is in the caseworker folder, as although it alternates between caseworker
// and solicitor (prof user), the test is to be run on the CCD ui, which the caseworker forlder is actually for

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStopEscalateIssue/caseProgressConfig');
const solicitorDetailsHtmlCheck = require('src/test/end-to-end/pages/caseProgressStopEscalateIssue/solicitorDetailsHtmlCheck');
const solCheckAnswersHtmlCheck = require('src/test/end-to-end/pages/caseProgressStopEscalateIssue/solCheckAnswersHtmlCheck');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('04 BO Case Progress E2E - stop/escalate/issue', async function (I) {
    try {
        // IDAM
        await I.authenticateWithIdamIfAvailable(true);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor, 0);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        /* eslint-disable no-console */
        console.info('Initial application entry');
        await I.caseProgressSolicitorDetails(caseProgressConfig);
        await I.caseProgressSolicitorDetailsCheckAnswers(caseProgressConfig, solicitorDetailsHtmlCheck.htmlCheck);
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
        await I.caseProgressClickElementsAndContinue([{css: '#willDispose-Yes'}, {css: '#englishWill-Yes'}, {css: '#appointExec-Yes'}]);
        await I.caseProgressStopEscalateIssueDeceasedDetailsCheck();
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 2,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Add application details',
            linkUrl: '/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1',
            goToNextStep: true});

        console.info('Add application details');
        await I.caseProgressClickElementsAndContinue([{css: '#willAccessOriginal-Yes'}, {css: '#willHasCodicils-No'}]);
        console.info('Dispense with notice and clearing type');
        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#dispenseWithNotice-No'}}, 
            {locator: {css: '#titleAndClearingType-TCTNoT'}},
            {locator: {css: '#titleAndClearingTypeNoT'}, text: 'Test details'},
        ]);  

        console.info('Remaining application details');
        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#primaryApplicantForenames'}, text: 'Fred'},
            {locator: {css: '#primaryApplicantSurname'}, text: 'Bassett'},
            {locator: {css: '#primaryApplicantHasAlias-No'}},
            {locator: {css: '#primaryApplicantIsApplying-Yes'}},
            {locator: {css: '#otherExecutorExists-No'}},
            {locator: {css: '#soleTraderOrLimitedCompany-Yes'}}]);

        await I.caseProgressWaitForElementThenContinue('#solsAdditionalInfo');
        await I.caseProgressCheckYourAnswers(solCheckAnswersHtmlCheck.htmlCheck);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 3,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Review and sign legal statement and submit application',
            linkUrl: '/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1',
            goToNextStep: true});

        console.info('Confirm application');
        await I.caseProgressClickElementsAndContinue([{css: '#solsSOTNeedToUpdate-No'}]);

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

        console.info('Stop case');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressNavigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChangeState('Stop case');
        await I.caseProgressStopEscalateIssueAddCaseStoppedReason();
        await I.caseProgressContinueWithoutChangingAnything();
        await I.waitForVisible({css: '#sign-out'});
        await I.waitForNavigationToComplete('#sign-out');

        console.info('Check progress tab for Case stopped');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef, 'Case stopped');
        await I.caseProgressStopEscalateIssueStoppedTabCheck();

        console.info('Escalate case to registrar');
        // log in as case worker
        await I.authenticateWithIdamIfAvailable(false, true);
        await I.caseProgressNavigateToCaseCaseworker(caseRef);
        await I.caseProgressCaseworkerChangeState('Escalate to registrar');
        await I.caseProgressClickGoAndSignOut();

        console.info('Check progress tab for Case escalated');
        // log back in as solicitor
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef, 'Registrar escalation');
        await I.caseProgressStopEscalateIssueEscalatedTabCheck();

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
        await I.caseProgressNavigateToCaseSolicitor(caseRef, 'Case Matching (Issue grant)');
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
        await I.caseProgressClickElementsAndContinue([{css: '#boSendToBulkPrint-No'}]);
        await I.caseProgressClickGoAndSignOut();

        console.info('Check progress tab for Issue grant');
        // log back in as solicitor & check all sections completed
        await I.authenticateWithIdamIfAvailable(true, true);
        await I.caseProgressNavigateToCaseSolicitor(caseRef, 'Grant issued');
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 8,
            numInProgress: 0,
            numNotStarted: 0,
            checkSubmittedDate: true,
            signOut: true});

        console.info('04 BO Case Progress E2E - stop/escalate/issue: complete');
    } catch (e) {
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }
}).retry(testConfig.TestRetryScenarios);
