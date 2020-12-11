'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const applyProbateConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/applyProbate/applyProbateConfig');
const deceasedDetailsConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/deceasedTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/historyTabConfig');

Feature('Solicitor - Apply Grant of probate').retry(testConfig.TestRetryFeatures);

Scenario('Solicitor - Apply Grant of probate (Will left annexed) - Stopped', async function (I) {
    const isSolicitorExecutor = true;
    const isSolicitorMainApplicant = true;
    const willType = 'WillLeftAnnexed';

    // IdAM
    await I.authenticateWithIdamIfAvailable();

    let nextStepName = 'Deceased details';
    let endState = 'Application created';
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor);
    await I.applyForProbatePage1();
    await I.applyForProbatePage2(isSolicitorExecutor, isSolicitorMainApplicant);
    await I.cyaPage();

    await I.seeEndState(endState);

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop()
        .match(/.{4}/g)
        .join('-');

    // eslint-disable-next-line no-console
    console.log('url is...', url);

    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, applyProbateConfig);

    endState = 'Admon will grant created';

    await I.chooseNextStep(nextStepName);
    await I.deceasedDetailsPage1();
    await I.deceasedDetailsPage2();
    await I.deceasedDetailsPage3(willType);
    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, deceasedDetailsConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, deceasedDetailsConfig);
    await I.seeUpdatesOnCase(caseRef, caseDetailsTabConfig, willType, deceasedDetailsConfig);

    nextStepName = 'Admon will details';
    endState = 'Stopped';
    await I.chooseNextStep(nextStepName);
    await I.admonWillDetailsPage1();
    await I.admonWillDetailsPage2();
    await I.admonWillDetailsPage3();
    await I.cyaPage();
    await I.admonWillDetailsPage4();

    await I.seeEndState(endState);

}).retry(testConfig.TestRetryScenarios);
