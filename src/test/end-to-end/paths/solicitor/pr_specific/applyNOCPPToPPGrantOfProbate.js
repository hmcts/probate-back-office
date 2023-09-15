'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const applyProbateConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/applyProbate/applyProbateConfig');
const gopConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/grantOfProbate/grantOfProbate');
const deceasedDetailsConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig');
const completeApplicationConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/completeApplication/completeApplication');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig');
const applicantExecutorDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/applicantExecDetailsTrustCorpTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/deceasedTabConfig');
const caseDetailsTabDeceasedDtlsConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfig');
const caseDetailsTabGopConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabGopTrustCorpConfig');
const caseDetailsTabUpdatesConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig');
const sotTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/sotTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/copiesTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/historyTabConfig');
const serviceRequestTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig');
const serviceRequestReviewTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig');
const nocApplicantDetailsConfig = require('src/test/end-to-end/pages/noticeOfChange/postNocApplicantDetailsConfig')
const changeOfRepresentativesTabConfig = require('src/test/end-to-end/pages/noticeOfChange/nocChangeOfRepresentativesTabConfig');
const changeOfRepresentativesDetailsConfig = require('src/test/end-to-end/pages/noticeOfChange/changeOfRepresentativesConfig');
const nocApplicantDetailsConfigAAT = require('src/test/end-to-end/pages/noticeOfChange/postNocApplicantDetailsConfigAAT')
const changeOfRepresentativesDetailsConfigAAT = require('src/test/end-to-end/pages/noticeOfChange/changeOfRepresentativesConfigAAT');
const nocConfig = require('src/test/end-to-end/pages/noticeOfChange/noticeOfChangeConfig');
const dateFns = require("date-fns");
const {legacyParse, convertTokens} = require("@date-fns/upgrade/v2");

Feature('Solicitor - Notice Of Change GOP').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Solicitor - Notice Of Change GOP';
Scenario(scenarioName, async function ({I}) {
    const isSolicitorNamedExecutor = true;
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

    await I.seeUpdatesOnCase(caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', applyProbateConfig, false);
    await I.seeCaseDetails(caseRef, sotTabConfig, completeApplicationConfig);

    nextStepName = 'Complete application';
    endState = 'Awaiting documentation';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.completeApplicationPage1();
    await I.completeApplicationPage2();
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
    await I.signOut();

    await I.logInfo(scenarioName, 'Login as PP user 2 to perform NoC');

    let env = '';
    let url = testConfig.TestBackOfficeUrl;
    if (url.includes("demo")) {
        env = 'Demo';
    } else {
        env = 'AAT';
    }

    nextStepName = 'Apply NoC Decision';
    endState = 'Awaiting documentation';
    await I.authenticateUserNoc(false);
    await I.nocNavigation();
    await I.nocPage1(caseRef);
    await I.nocPage2(deceasedDetailsConfig.page1_surname);
    await I.nocPage3(caseRef, deceasedDetailsConfig.page1_surname);
    await I.nocConfirmationPage(caseRef);

    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, applicantExecutorDetailsTabConfig, gopDtlsAndDcsdDtls);

    if (env === 'Demo') {
        changeOfRepresentativesDetailsConfig.nocTriggeredDate = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
        await I.seeUpdatesOnCase(caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', nocApplicantDetailsConfig, false);
        await I.seeCaseDetails(caseRef, changeOfRepresentativesTabConfig, changeOfRepresentativesDetailsConfig);
    }
    else {
        changeOfRepresentativesDetailsConfigAAT.nocTriggeredDate = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));
        await I.seeUpdatesOnCase(caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', nocApplicantDetailsConfigAAT, false);
        await I.seeCaseDetails(caseRef, changeOfRepresentativesTabConfig, changeOfRepresentativesDetailsConfigAAT);
    }

    await I.signOut();

    await I.logInfo(scenarioName, 'Login as PP user 1 to verify NoC');
    await I.authenticateWithIdamIfAvailable(true);
    await I.navigateToCase(caseRef);
    await I.waitForText(nocConfig.nocWaitForText, testConfig.WaitForTextTimeout);
    await I.see(nocConfig.nocWaitForText);
    await I.dontSee(caseRef);
}).retry(testConfig.TestRetryScenarios);
