'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const applyProbateConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/applyProbate/applyProbateConfig');
const deceasedDetailsConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig');
const admonWillDetailsConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/admonWillDetails/admonWillDetails');
const completeApplicationConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/completeApplication/completeApplication');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/deceasedTabConfigEE');
const iHTTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/iHTTabConfigEE400');
const caseDetailsTabDeceasedDtlsConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfigEEIHT400');
const caseDetailsTabAdmonWillConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabAdmonWillConfig');
const caseDetailsTabUpdatesConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig');

const sotTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/sotTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/copiesTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/historyTabConfig');
const serviceRequestTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig');
const serviceRequestReviewTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStandard/caseProgressConfig');

Feature('Solicitor - Apply Grant of probate Admon Will Excepted Estates').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Solicitor - Apply Grant of probate - Admon Will Excepted Estates (Will left annexed)';
Scenario(scenarioName, async function ({I}) {

    const updateAddressManually = true;
    const willType = 'WillLeftAnnexed';

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

    endState = 'Admon will grant created';

    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.deceasedDetailsPage1('EE');
    await I.deceasedDetailsPage2('EE', 'Yes', 'IHT400');
    await I.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
    await I.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT400');
    await I.deceasedDetailsPage3(willType);
    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, deceasedDetailsConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig);
    await I.seeCaseDetails(caseRef, iHTTabConfig, deceasedDetailsConfig);
    await I.seeUpdatesOnCase(caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig);

    nextStepName = 'Admon will details';
    endState = 'Application updated';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.admonWillDetailsPage1();
    await I.admonWillDetailsPage2(updateAddressManually);
    await I.admonWillDetailsPage3();
    await I.admonWillDetailsPage4();
    await I.admonWillDetailsPage5();

    await I.cyaPage();

    await I.seeEndState(endState);
    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);

    const admonWillDtlsAndDcsdDtls = {...deceasedDetailsConfig, ...admonWillDetailsConfig};

    await I.seeCaseDetails(caseRef, caseDetailsTabDeceasedDtlsConfig, admonWillDtlsAndDcsdDtls);
    await I.seeCaseDetails(caseRef, caseDetailsTabAdmonWillConfig, admonWillDtlsAndDcsdDtls);

    await I.seeUpdatesOnCase(caseRef, sotTabConfig, willType, completeApplicationConfig);
    await I.seeUpdatesOnCase(caseRef, applicantDetailsTabConfig, 'Applicant', admonWillDetailsConfig);

    nextStepName = 'Complete application';
    endState = 'Awaiting documentation';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.completeApplicationPage1(willType);
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

}).retry(testConfig.TestRetryScenarios);
