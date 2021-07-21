'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const applyProbateConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/applyProbate/applyProbateConfig');
const deceasedDetailsConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig');
const intestacyDetailsConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/intestacyDetails/intestacyDetails');
const completeApplicationConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/completeApplication/completeApplication');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/deceasedTabConfig');
const caseDetailsTabDeceasedDtlsConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfig');
const caseDetailsTabIntestacyConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabIntestacyConfig');
const caseDetailsTabUpdatesConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig');

const sotTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/sotTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/copiesTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/historyTabConfig');

Feature('Solicitor - Apply Grant of probate').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Solicitor - Apply Grant of probate - No Will (Intestacy)';
Scenario(scenarioName, async function ({I}) {

    const willType = 'NoWill';

    await I.logInfo(scenarioName, 'Login as Solicitor');
    await I.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Deceased details';
    let endState = 'Application created';
    await I.logInfo(scenarioName, nextStepName);
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    await I.applyForProbatePage1();
    await I.applyForProbatePage2();
    await I.cyaPage();

    await I.seeEndState(endState);

    const caseRef = await I.getCaseRefFromUrl();

    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, applyProbateConfig);

    endState = 'Intestacy grant created';

    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.deceasedDetailsPage1();
    await I.deceasedDetailsPage2();
    await I.deceasedDetailsPage3('NoWill');
    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, deceasedDetailsConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig);
    await I.seeUpdatesOnCase(caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig);

    nextStepName = 'Intestacy details';
    endState = 'Application updated';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.intestacyDetailsPage1();
    await I.intestacyDetailsPage2();
    await I.intestacyDetailsPage3();
    await I.intestacyDetailsPage4();
    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);

    const inDtlsAndDcsdDtls = {...deceasedDetailsConfig, ...intestacyDetailsConfig};
    await I.seeCaseDetails(caseRef, caseDetailsTabDeceasedDtlsConfig, inDtlsAndDcsdDtls);
    await I.seeCaseDetails(caseRef, caseDetailsTabIntestacyConfig, inDtlsAndDcsdDtls);
    await I.seeUpdatesOnCase(caseRef, sotTabConfig, willType, completeApplicationConfig);
    await I.seeUpdatesOnCase(caseRef, caseDetailsTabUpdatesConfig, 'MaritalStatus', inDtlsAndDcsdDtls);
    await I.seeUpdatesOnCase(caseRef, applicantDetailsTabConfig, 'Applicant', inDtlsAndDcsdDtls);

    nextStepName = 'Complete application';
    endState = 'Case created';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.completeApplicationPage1(willType);
    await I.completeApplicationPage2();
    await I.completeApplicationPage3();
    await I.completeApplicationPage4();
    await I.completeApplicationPage5();
    await I.completeApplicationPage6();
    await I.completeApplicationPage7();
    await I.completeApplicationPage8();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, copiesTabConfig, completeApplicationConfig);

}).retry(testConfig.TestRetryScenarios);
