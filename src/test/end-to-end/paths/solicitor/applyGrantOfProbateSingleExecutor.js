'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const applyProbateConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/applyProbate/applyProbateConfig');
const gopConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/grantOfProbate/grantOfProbate');
const deceasedDetailsConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig');
const completeApplicationConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/completeApplication/completeApplication');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/deceasedTabConfig');
const caseDetailsTabDeceasedDtlsConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfig');
const caseDetailsTabGopConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabGopTrustCorpConfig');
const caseDetailsTabUpdatesConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig');
const sotTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/sotTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/copiesTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/historyTabConfig');

Feature('Solicitor - Apply Grant of probate').retry(testConfig.TestRetryFeatures);

Scenario('03 - Solicitor - Apply Grant of probate Single Executor', async function (I) {
    const isSolicitorNamedExecutor = true;
    const isSolicitorApplyingExecutor = true;
    const willType = 'WillLeft';

    // IdAM
    await I.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Deceased details';
    let endState = 'Application created';
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    await I.applyForProbatePage1();
    await I.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await I.cyaPage();

    await I.seeEndState(endState);

    const caseRef = await I.getCaseRefFromUrl();

    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, applyProbateConfig);

    endState = 'Grant of probate created';

    await I.chooseNextStep(nextStepName);
    await I.deceasedDetailsPage1();
    await I.deceasedDetailsPage2();
    await I.deceasedDetailsPage3();
    await I.deceasedDetailsPage4();
    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, deceasedDetailsConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig);
    await I.dontSeeCaseDetails(caseDetailsTabDeceasedDtlsConfig.fieldsNotPresent);
    await I.seeUpdatesOnCase(caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig, false);

    nextStepName = 'Grant of probate details';
    endState = 'Application updated';
    await I.chooseNextStep(nextStepName);
    await I.grantOfProbatePage1();
    await I.grantOfProbatePage2(true, isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await I.grantOfProbatePage3();
    await I.grantOfProbatePage4(isSolicitorApplyingExecutor);
    await I.grantOfProbatePage5();
    await I.grantOfProbatePage6();
    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);

    const gopDtlsAndDcsdDtls = {...deceasedDetailsConfig, ...gopConfig};
    await I.seeCaseDetails(caseRef, caseDetailsTabDeceasedDtlsConfig, gopDtlsAndDcsdDtls);
    await I.seeCaseDetails(caseRef, caseDetailsTabGopConfig, gopDtlsAndDcsdDtls);
    await I.seeUpdatesOnCase(caseRef, caseDetailsTabUpdatesConfig, willType, gopDtlsAndDcsdDtls, true);
    await I.dontSeeCaseDetails(caseDetailsTabDeceasedDtlsConfig.fieldsNotPresent);

    await I.seeUpdatesOnCase(caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', applyProbateConfig, false);
    await I.seeCaseDetails(caseRef, sotTabConfig, completeApplicationConfig);

    nextStepName = 'Complete application';
    endState = 'Case created';
    await I.chooseNextStep(nextStepName);
    await I.completeApplicationPage1();
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
