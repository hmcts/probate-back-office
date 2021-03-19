'use strict';

// This test is in the caseworker folder, as although it alternates between caseworker
// and solicitor (prof user), the test is to be run on the CCD ui, which the caseworker forlder is actually for
const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressAppStopped/caseProgressConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('01 BO Case Progress E2E - application stopped path', async function ({I}) {
    // IDAM
    /* eslint-disable no-console */
    try {
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
        await I.caseProgressClickElementsAndContinue([{css: '#willDispose-Yes'}, {css: '#englishWill-Yes'}, {css: '#appointExec-No'}, {css: '#appointExecNo-No'}]);
        await I.caseProgressStandardDeceasedDetailsCheck();
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 2,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Add application details',
            linkUrl: '/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1',
            goToNextStep: true});

        console.info('Add application details');
        await I.caseProgressClickElementsAndContinue([{css: '#willAccessOriginal-Yes'}, {css: '#willHasCodicils-No'}]);
        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#primaryApplicantForenames'}, text: 'Fred'},
            {locator: {css: '#primaryApplicantSurname'}, text: 'Bassett'},
            {locator: {css: '#primaryApplicantHasAlias-No'}},
            {locator: {css: '#primaryApplicantIsApplying-No'}},
            {locator: {css: '#solsPrimaryExecutorNotApplyingReason'}, option: '6: MentallyIncapable'},
            {locator: {css: '#otherExecutorExists-No'}}]);

        await I.caseProgressWaitForElementThenContinue('#solsAdditionalInfo');

        // More extensive checks already performed at this stage for stop/escalate issue
        console.info('Check answers');
        await I.caseProgressCheckYourAnswers();

        console.info('App stopped details');
        await I.caseProgressAppStoppedDetails();

        console.info('App stopped tab check');
        await I.caseProgressAppStoppedTabCheck();

        console.info('01 BO Case Progress E2E - application stopped: complete');

    } catch (e) {
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }
}).retry(testConfig.TestRetryScenarios);
