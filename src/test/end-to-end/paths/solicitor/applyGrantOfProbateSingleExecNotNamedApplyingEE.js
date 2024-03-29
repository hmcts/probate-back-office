'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const applyProbateConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/applyProbate/applyProbateConfig');
const gopConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/grantOfProbate/grantOfProbate');
const deceasedDetailsConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig');
const completeApplicationConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/completeApplication/completeApplication');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig');
const applicantExecutorDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/applicantExecDetailsTrustCorpTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/deceasedTabConfigEE');
const iHTTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/iHTTabConfigEE400');
const caseDetailsTabDeceasedDtlsConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfigEEIHT400');
const caseDetailsTabGopConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabGopTrustCorpConfig');
const caseDetailsTabUpdatesConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig');
const sotTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/sotTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/copiesTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/historyTabConfig');
const serviceRequestTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig');
const serviceRequestReviewTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStandard/caseProgressConfig');

Feature('Solicitor - Apply Grant of probate Excepted Estates').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Solicitor - Apply Grant of probate Single Executor Excepted Estates (not named, applying)';

Scenario(scenarioName, async function ({I}) {
    const isSolicitorNamedExecutor = false;
    const isSolicitorApplyingExecutor = true;
    const willType = 'WillLeft';

    await I.logInfo(scenarioName, 'Login as Solicitor');
    await I.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Deceased details';
    let endState = 'Application created';
    await I.logInfo(scenarioName, nextStepName);
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    await I.applyForProbatePage1();
    await I.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await I.cyaPage();

    await I.seeEndState(endState);

    const caseRef = await I.getCaseRefFromUrl();

    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, applyProbateConfig);

    endState = 'Grant of probate created';

    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.deceasedDetailsPage1('EE');
    await I.deceasedDetailsPage2('EE', 'Yes', 'IHT400');
    await I.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
    await I.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT400');
    await I.deceasedDetailsPage3();
    await I.deceasedDetailsPage4();
    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, deceasedDetailsConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig);
    await I.seeCaseDetails(caseRef, iHTTabConfig, deceasedDetailsConfig);
    await I.dontSeeCaseDetails(caseDetailsTabDeceasedDtlsConfig.fieldsNotPresent);
    await I.seeUpdatesOnCase(caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig);

    nextStepName = 'Grant of probate details';
    endState = 'Application updated';
    await I.logInfo(scenarioName, nextStepName, caseRef);
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

    await I.seeUpdatesOnCase(caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', applyProbateConfig);
    await I.seeCaseDetails(caseRef, sotTabConfig, completeApplicationConfig);

    nextStepName = 'Complete application';
    endState = 'Awaiting documentation';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.completeApplicationPage1();
    await I.completeApplicationPage3();
    await I.completeApplicationPage4();
    await I.completeApplicationPage5();
    await I.completeApplicationPage6();
    await I.completeApplicationPage7();
    await I.completeApplicationPage8();

    await I.logInfo(scenarioName, 'Payment');
    await I.makePaymentPage1(caseRef, serviceRequestTabConfig);
    await I.reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig);
    await I.makePaymentPage2(caseRef);
    await I.viewPaymentStatus(caseRef);

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, copiesTabConfig, completeApplicationConfig);
    await I.seeCaseDetails(caseRef, applicantExecutorDetailsTabConfig, gopDtlsAndDcsdDtls);
}).retry(testConfig.TestRetryScenarios);
