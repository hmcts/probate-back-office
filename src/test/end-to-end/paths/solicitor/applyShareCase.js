'use strict';
const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

Feature('Solicitor - Share A Case').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Solicitor - Share A Case';

Scenario(scenarioName, async function ({I}) {
    if(testConfig.TestBackOfficeUrl.includes("aat")) {
        const isSolicitorNamedExecutor = true;
        const isSolicitorApplyingExecutor = true;

        await I.logInfo(scenarioName, 'Login as PP user 1');
        await I.authenticateUserShareCase(true);

        let nextStepName = 'Deceased details';
        let endState = 'Application created';

        await I.logInfo(scenarioName, nextStepName);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
        await I.applyForProbatePage1();
        await I.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
        await I.cyaPage();
        await I.shareCaseSelection(false);
        await I.logInfo(scenarioName, 'Login as PP user 2');
        await I.authenticateUserShareCase();
        await I.verifyShareCase();
        await I.logInfo(scenarioName, 'Login as PP user 1');
        await I.authenticateUserShareCase(true);
        await I.shareCaseVerifyUserRemove();
        await I.logInfo(scenarioName, 'Login as PP user 2');
        await I.authenticateUserShareCase();
        await I.shareCaseDelete();

    }

}).retry(testConfig.TestRetryScenarios);
