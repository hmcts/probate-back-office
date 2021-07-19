'use strict';

// This test is in the caseworker folder, as although it alternates between caseworker
// and solicitor (prof user), the test is to be run on the CCD ui, which the caseworker folder is actually for
const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressAppStopped/caseProgressConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

const scenarioName = 'Case Progress - application stopped path';
/* eslint-disable no-console */
Scenario(scenarioName, async function ({I}) {
    try {
        // get unique suffix for names - in order to match only against 1 case
        const unique_deceased_user = Date.now();
        await I.logInfo(scenarioName, 'Login as Solicitor');                
        await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.solGor);
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
        await I.caseProgressClickElementsAndContinue([{css: '#willDispose_Yes'}, {css: '#englishWill_Yes'}, {css: '#appointExec_No'}, {css: '#appointExecNo_No'}]);
        await I.caseProgressStandardDeceasedDetailsCheck(unique_deceased_user);
        await I.caseProgressCheckCaseProgressTab({
            numCompleted: 2,
            numInProgress: 0,
            numNotStarted: 1,
            linkText: 'Add application details',
            linkUrl: '/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1',
            goToNextStep: true});

        await I.logInfo(scenarioName,'Add application details');
        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#willAccessOriginal_Yes'}},
            {locator: {css: '#originalWillSignedDate-day'}, text: '10'},
            {locator: {css: '#originalWillSignedDate-month'}, text: '10'},
            {locator: {css: '#originalWillSignedDate-year'}, text: '2018'},
            {locator: {css: '#willHasCodicils_No'}}]);

        await I.logInfo(scenarioName,'Dispense with notice and clearing type');
        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#dispenseWithNotice_No'}},
            {locator: {css: '#titleAndClearingType-TCTNoT'}},
        ]);

        await I.logInfo(scenarioName,'Remaining application details');

        await I.caseProgressClickSelectOrFillElementsAndContinue([
            {locator: {css: '#primaryApplicantForenames'}, text: 'Fred'},
            {locator: {css: '#primaryApplicantSurname'}, text: 'Bassett'},
            {locator: {css: '#primaryApplicantHasAlias_No'}},
            {locator: {css: '#primaryApplicantIsApplying_No'}},
            {locator: {css: '#solsPrimaryExecutorNotApplyingReason_MentallyIncapable'}},
            {locator: {css: '#otherExecutorExists_No'}}]);

        await I.caseProgressWaitForElementThenContinue('#furtherEvidenceForApplication');
        await I.caseProgressWaitForElementThenContinue('#solsAdditionalInfo');

        // More extensive checks already performed at this stage for stop/escalate issue
        await I.logInfo(scenarioName,'Check answers');
        await I.caseProgressCheckYourAnswers();

        await I.logInfo(scenarioName,'App stopped details');
        await I.caseProgressAppStoppedDetails();

        await I.logInfo(scenarioName,'App stopped tab check');
        await I.caseProgressAppStoppedTabCheck();

        await I.logInfo(scenarioName,'01 BO Case Progress E2E - application stopped: complete');

    } catch (e) {
        //  await I.printPageAsScreenshot('cpaps');
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }
}).retry(testConfig.TestRetryScenarios);
