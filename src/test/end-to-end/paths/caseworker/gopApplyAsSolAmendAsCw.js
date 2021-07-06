'use strict';

// As per ExUi Solicitor test applyGrantOfProbateSingleExecutor.js but runs in ccd as needs
// to sign out then log back in as a caseworker to amend details

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

Feature('Back office').retry(testConfig.TestRetryFeatures);

/* eslint-disable no-console */
Scenario('09 - Solicitor - Apply Grant of probate Single Executor', async function ({I}) {
    const isSolicitorNamedExecutor = true;
    const isSolicitorApplyingExecutor = true;
    const willType = 'WillLeft';

    // IdAM - Solicitor
    await I.authenticateWithIdamIfAvailable(true);

    console.info('Initial application details');

    let nextStepName = 'Deceased details';
    let endState = 'Application created';
    await I.selectNewCase(true);
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor, true);
    await I.applyForProbatePage1();
    await I.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await I.cyaPage();

    await I.seeEndState(endState);

    const caseRef = await I.getCaseRefFromUrl(true);

    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState, true);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, applyProbateConfig, null, null, true);

    endState = 'Grant of probate created';

    console.info('Deceased details');

    await I.chooseNextStep(nextStepName, true);
    await I.deceasedDetailsPage1();
    await I.deceasedDetailsPage2();
    await I.deceasedDetailsPage3();
    await I.deceasedDetailsPage4();
    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState, true);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, deceasedDetailsConfig, null, null, true);
    await I.seeCaseDetails(caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig, null, null, true);
    await I.dontSeeCaseDetails(caseDetailsTabDeceasedDtlsConfig.fieldsNotPresent);
    await I.seeUpdatesOnCase(caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig);

    console.info('Grant of probate details');

    nextStepName = 'Grant of probate details';
    endState = 'Application updated';
    await I.chooseNextStep(nextStepName, true);
    await I.grantOfProbatePage1();
    await I.grantOfProbatePage2(false, isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await I.grantOfProbatePage3();
    await I.grantOfProbatePage4(isSolicitorApplyingExecutor);
    await I.grantOfProbatePage5();
    await I.grantOfProbatePage6();
    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState, true);

    const gopDtlsAndDcsdDtls = {...deceasedDetailsConfig, ...gopConfig};
    await I.seeCaseDetails(caseRef, caseDetailsTabDeceasedDtlsConfig, gopDtlsAndDcsdDtls, null, null, true);
    await I.seeCaseDetails(caseRef, caseDetailsTabGopConfig, gopDtlsAndDcsdDtls, null, null, true);

    await I.seeUpdatesOnCase(caseRef, caseDetailsTabUpdatesConfig, willType, gopDtlsAndDcsdDtls, true, true);
    await I.dontSeeCaseDetails(caseDetailsTabDeceasedDtlsConfig.fieldsNotPresent);

    await I.seeUpdatesOnCase(caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', applyProbateConfig, false, true);
    await I.seeCaseDetails(caseRef, sotTabConfig, completeApplicationConfig, null, null, true);

    console.info('Complete application');

    nextStepName = 'Complete application';
    endState = 'Case created';
    await I.chooseNextStep(nextStepName, true);
    await I.completeApplicationPage1();
    await I.completeApplicationPage2();
    await I.completeApplicationPage3();
    await I.completeApplicationPage4();
    await I.completeApplicationPage5();
    await I.completeApplicationPage6();
    await I.completeApplicationPage7();
    await I.completeApplicationPage8();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState, true);
    await I.seeCaseDetails(caseRef, copiesTabConfig, completeApplicationConfig, null, null, true);

    console.info('Sign out and login as case worker');

    await I.waitForNavigationToComplete(testConfig.XuiSignoutCssSelector, true);

    // IdAM - Caseworker
    await I.authenticateWithIdamIfAvailable(false);
    await I.navigateToCaseCaseworker(caseRef);
    console.info('Amend details');
    nextStepName = 'Amend case details';
    await I.chooseNextStep(nextStepName);
    await I.enterGrantOfProbatePage1('update', true);
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.checkAmendApplicantDetailsForSolCreatedApp();
    await I.checkMyAnswers(nextStepName);
    await I.chooseNextStep(nextStepName);
    await I.checkAmendDomAndAssetsForSolCreatedApp();
    await I.checkMyAnswers(nextStepName);
}).retry(testConfig.TestRetryScenarios);
