import dateFns from "date-fns";
import { test } from "../../Fixtures/fixtures.ts";

import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import { testConfig } from "../../Configs/config.ts";

import applyProbateConfig from "../../Pages/solicitorApplyProbate/applyProbate/applyProbateConfig.json" with { type: "json" };
import gopConfig from "../../Pages/solicitorApplyProbate/grantOfProbate/grantOfProbate.json" with { type: "json" };
import deceasedDetailsConfig from "../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };
import completeApplicationConfig from "../../Pages/solicitorApplyProbate/completeApplication/completeApplication.json" with { type: "json" };

import applicantDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig.json" with { type: "json" };
import applicantExecutorDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/applicantExecDetailsTrustCorpTabConfig.json" with { type: "json" };
import deceasedTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/deceasedTabConfigEE.json" with { type: "json" };
import iHTTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/iHTTabConfigEE400.json" with { type: "json" };
import caseDetailsTabDeceasedDtlsConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfigEE.json" with { type: "json" };
import caseDetailsTabGopConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabGopTrustCorpConfig.json" with { type: "json" };
import caseDetailsTabUpdatesConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig.json" with { type: "json" };
import sotTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/sotTabConfig.json" with { type: "json" };
import copiesTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/copiesTabConfig.json" with { type: "json" };
import historyTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/historyTabConfig.json" with { type: "json" };
import serviceRequestTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig.json" with { type: "json" };
import serviceRequestReviewTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig.json" with { type: "json" };
import nocApplicantDetailsConfig from "../../Pages/noticeOfChange/postNocApplicantDetailsConfig.json" with { type: "json" };
import changeOfRepresentativesTabConfig from "../../Pages/noticeOfChange/nocChangeOfRepresentativesTabConfig.json" with { type: "json" };
import changeOfRepresentativesDetailsConfig from "../../Pages/noticeOfChange/changeOfRepresentativesConfig.json" with { type: "json" };
import nocApplicantDetailsConfigAAT from "../../Pages/noticeOfChange/postNocApplicantDetailsConfigAAT.json" with { type: "json" };
import changeOfRepresentativesDetailsConfigAAT from "../../Pages/noticeOfChange/changeOfRepresentativesConfigAAT.json" with { type: "json" };
import caseProgressConfig from "../../Pages/caseProgressStandard/caseProgressConfig.json" with { type: "json" };

test.describe("Solicitor - Notice Of Change GOP", () => {
  test("Solicitor - Notice Of Change GOP", async ({
                                            basePage,
                                            signInPage,
                                            createCasePage,
                                            solCreateCasePage,
                                            cwEventActionsPage,
                                          }, testInfo) => {
    const scenarioName = 'Solicitor - Notice Of Change GOP';
    const isSolicitorNamedExecutor = true;
    const isSolicitorApplyingExecutor = true;
    const willType = 'WillLeft';

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'Login as Solicitor');
    await signInPage.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Apply for probate';
    let endState = 'Application created (deceased details)';
    // @ts-ignore
    await basePage.logInfo(scenarioName, nextStepName);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    await solCreateCasePage.applyForProbatePage1();
    await solCreateCasePage.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    const caseRef = await basePage.getCaseRefFromUrl();

    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, applicantDetailsTabConfig, applyProbateConfig);

    nextStepName = 'Deceased details';
    endState = 'Grant of probate created';

    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.deceasedDetailsPage1('EE');
    await solCreateCasePage.deceasedDetailsPage2('EE', 'Yes', 'IHT400');
    await solCreateCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
    await solCreateCasePage.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT400');
    await solCreateCasePage.deceasedDetailsPage3();
    await solCreateCasePage.deceasedDetailsPage4();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, deceasedTabConfig, deceasedDetailsConfig);
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig);
    await basePage.seeCaseDetails(testInfo, caseRef, iHTTabConfig, deceasedDetailsConfig);
    await basePage.dontSeeCaseDetails(caseDetailsTabDeceasedDtlsConfig.fieldsNotPresent);
    await basePage.seeUpdatesOnCase(testInfo, caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig, false);

    nextStepName = 'Grant of probate details';
    endState = 'Application updated';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.grantOfProbatePage1();
    await solCreateCasePage.grantOfProbatePage2(true, isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await solCreateCasePage.grantOfProbatePage3();
    await solCreateCasePage.grantOfProbatePage4(isSolicitorApplyingExecutor);
    await solCreateCasePage.grantOfProbatePage5();
    await solCreateCasePage.grantOfProbatePage6();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);

    const gopDtlsAndDcsdDtls = {...deceasedDetailsConfig, ...gopConfig};
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabDeceasedDtlsConfig, gopDtlsAndDcsdDtls);
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabGopConfig, gopDtlsAndDcsdDtls);
    await basePage.seeUpdatesOnCase(testInfo, caseRef, caseDetailsTabUpdatesConfig, willType, gopDtlsAndDcsdDtls, true);
    await basePage.dontSeeCaseDetails(caseDetailsTabDeceasedDtlsConfig.fieldsNotPresent);

    await basePage.seeUpdatesOnCase(testInfo, caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', applyProbateConfig, false);
    await basePage.seeCaseDetails(testInfo, caseRef, sotTabConfig, completeApplicationConfig);

    nextStepName = 'Complete application';
    endState = 'Awaiting documentation';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.completeApplicationPage1();
    await solCreateCasePage.completeApplicationPage3();
    await solCreateCasePage.completeApplicationPage4();
    await solCreateCasePage.completeApplicationPage5();
    await solCreateCasePage.completeApplicationPage6();
    await solCreateCasePage.completeApplicationPage7();
    await solCreateCasePage.completeApplicationPage8();

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'Payment');
    await solCreateCasePage.makePaymentPage1(caseRef, serviceRequestTabConfig);
    await solCreateCasePage.reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig);
    await solCreateCasePage.makePaymentPage2(caseRef);
    // @ts-ignore
    await solCreateCasePage.viewPaymentStatus(testInfo, caseRef);

    await solCreateCasePage.seeEndState(endState);
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, copiesTabConfig, completeApplicationConfig);
    await basePage.seeCaseDetails(testInfo, caseRef, applicantExecutorDetailsTabConfig, gopDtlsAndDcsdDtls);
    await signInPage.signOut();

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'Login as PP user 2 to perform NoC');

    let env = '';
    const url = testConfig.TestBackOfficeUrl;
    if (url.includes('demo')) {
      env = 'Demo';
    } else {
      env = 'AAT';
    }

    nextStepName = 'Apply NoC Decision';
    endState = 'Awaiting documentation';
    await signInPage.authenticateUserNoc(false);
    await solCreateCasePage.nocNavigation();
    await solCreateCasePage.nocPage1(caseRef);
    await solCreateCasePage.nocPage2(deceasedDetailsConfig.page1_surname);
    await solCreateCasePage.nocPage3(caseRef, deceasedDetailsConfig.page1_surname);
    await solCreateCasePage.nocConfirmationPage(caseRef);

    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, applicantExecutorDetailsTabConfig, gopDtlsAndDcsdDtls);

    if (env === 'Demo') {
      changeOfRepresentativesDetailsConfig.nocTriggeredDate = dateFns.format(new Date(), testConfig.dateFormat);
      await basePage.seeUpdatesOnCase(testInfo, caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', nocApplicantDetailsConfig, false);
      await basePage.seeCaseDetails(testInfo, caseRef, changeOfRepresentativesTabConfig, changeOfRepresentativesDetailsConfig, 'changeOfRepresentative', endState,1, true);
    } else {
      changeOfRepresentativesDetailsConfigAAT.nocTriggeredDate = dateFns.format(new Date(), testConfig.dateFormat);
      await basePage.seeUpdatesOnCase(testInfo, caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', nocApplicantDetailsConfigAAT, false);
      await basePage.seeCaseDetails(testInfo, caseRef, changeOfRepresentativesTabConfig, changeOfRepresentativesDetailsConfigAAT,'changeOfRepresentative', endState,1, true);
    }

    await signInPage.signOut();

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'Login as PP user 1 to verify NoC');
    await signInPage.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.verifyNoc(caseRef);
  });
});
