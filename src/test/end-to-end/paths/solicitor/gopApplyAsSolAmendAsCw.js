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
const scenarioName = 'Solicitor Create/Caseworker Amend GoR - Grant issued';
/* eslint-disable no-console */
Scenario(scenarioName, async function ({I}) {
    // get unique suffix for names - in order to match only against 1 case
    // const unique_deceased_user = Date.now();

    const isSolicitorNamedExecutor = true;
    const isSolicitorApplyingExecutor = true;
    const willType = 'WillLeft';

    await I.logInfo(scenarioName, 'Login as Solicitor');
    await I.authenticateWithIdamIfAvailable(true);

    await I.logInfo(scenarioName, 'Initial application details');

    let nextStepName = 'Deceased details';
    await I.logInfo(scenarioName, nextStepName);

    let endState = 'Application created';
    await I.logInfo(scenarioName, nextStepName);
    await I.selectNewCase(true);
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor, true);
    await I.applyForProbatePage1();
    await I.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await I.cyaPage();

    await I.seeEndState(endState);

    const caseRef = await I.getCaseRefFromUrl(true);

    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState, true);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, applyProbateConfig, null, null, true);

    endState = 'Grant of probate created';

    await I.logInfo(scenarioName, 'Deceased details');

    await I.chooseNextStep(nextStepName, true);
    await I.logInfo(scenarioName, nextStepName);
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

    await I.logInfo(scenarioName, 'Grant of probate details');

    nextStepName = 'Grant of probate details';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    endState = 'Application updated';
    await I.chooseNextStep(nextStepName, true);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage1');
    await I.grantOfProbatePage1();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage2');
    await I.grantOfProbatePage2(false, isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage3');
    await I.grantOfProbatePage3();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage4');
    await I.grantOfProbatePage4(isSolicitorApplyingExecutor);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage5');
    await I.grantOfProbatePage5();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage6');
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

    await I.logInfo(scenarioName, 'Complete application');

    nextStepName = 'Complete application';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    endState = 'Case created';
    await I.chooseNextStep(nextStepName, true);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage1');
    await I.completeApplicationPage1();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage2');
    await I.completeApplicationPage2();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage3');
    await I.completeApplicationPage3();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage4');
    await I.completeApplicationPage4();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage5');
    await I.completeApplicationPage5();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage6');
    await I.completeApplicationPage6();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage7');
    await I.completeApplicationPage7();
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage8');
    await I.completeApplicationPage8();

    await I.logInfo(scenarioName, endState);
    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState, true);
    await I.seeCaseDetails(caseRef, copiesTabConfig, completeApplicationConfig, null, null, true);

    await I.logInfo(scenarioName, 'Sign out and login as case worker');

    await I.signOut();

    // IdAM - Caseworker
    await I.authenticateWithIdamIfAvailable(false);
    await I.navigateToCase(caseRef);
    await I.logInfo(scenarioName, 'Amend details', caseRef);
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
