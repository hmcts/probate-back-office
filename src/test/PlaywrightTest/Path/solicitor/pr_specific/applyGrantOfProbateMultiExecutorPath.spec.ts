import { test } from "../../../Fixtures/fixtures.ts";

import createCaseConfig from "../../../Pages/createCase/createCaseConfig.json" with { type: "json" };

import applyProbateConfig from "../../../Pages/solicitorApplyProbate/applyProbate/applyProbateConfig.json" with { type: "json" };
import deceasedDetailsConfig from "../../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };
import gopConfig from "../../../Pages/solicitorApplyProbate/grantOfProbate/grantOfProbate.json" with { type: "json" };
import completeApplicationConfig from "../../../Pages/solicitorApplyProbate/completeApplication/completeApplication.json" with { type: "json" };

import applicantDetailsTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig.json" with { type: "json" };
import applicantExecutorDetailsTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/applicantExecDetailsTrustCorpTabConfig.json" with { type: "json" };
import deceasedTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/deceasedTabConfig.json" with { type: "json" };
import caseDetailsTabDeceasedDtlsConfig from "../../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfigMulti.json" with { type: "json" };
import caseDetailsTabGopConfig from "../../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabGopTrustCorpConfig.json" with { type: "json" };
import caseDetailsTabUpdatesConfig from "../../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig.json" with { type: "json" };
import sotTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/sotTabConfig.json" with { type: "json" };
import copiesTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/copiesTabConfig.json" with { type: "json" };
import historyTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/historyTabConfig.json" with { type: "json" };
import serviceRequestTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig.json" with { type: "json" };
import serviceRequestReviewTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig.json" with { type: "json" };

test.describe("Solicitor - Apply Grant of probate", () => {
    test("Solicitor - Apply Grant of probate Multi Executor @edge", async ({
      basePage,
      signInPage,
      createCasePage,
      solCreateCasePage,
      cwEventActionsPage
    }, testInfo) => {
      const scenarioName = "Solicitor - Apply Grant of probate Multi Executor";
      const willType = 'WillLeft';
      await basePage.logInfo(scenarioName, 'Login as Solicitor', null);
      await signInPage.authenticateWithIdamIfAvailable(true);

      let nextStepName = 'Apply for probate';
      let endState = 'Application created (deceased details)';
      await createCasePage.selectNewCase();
      await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
      await solCreateCasePage.applyForProbatePage1();
      await solCreateCasePage.applyForProbatePage2(true, true);
      await solCreateCasePage.cyaPage();

      await solCreateCasePage.seeEndState(endState);

      const caseRef = await basePage.getCaseRefFromUrl();

      await basePage.logInfo(scenarioName, nextStepName, caseRef);
      await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
      await basePage.seeCaseDetails(testInfo, caseRef, applicantDetailsTabConfig, applyProbateConfig);

      nextStepName = 'Deceased details';
      endState = 'Grant of probate created';
      await cwEventActionsPage.chooseNextStep(nextStepName);
      await solCreateCasePage.deceasedDetailsPage1();
      await solCreateCasePage.deceasedDetailsPage2('MultiExec');
      await solCreateCasePage.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue);
      await solCreateCasePage.deceasedDetailsPage3();
      await solCreateCasePage.deceasedDetailsPage4();
      await solCreateCasePage.cyaPage();

      await solCreateCasePage.seeEndState(endState);
      await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
      await basePage.seeCaseDetails(testInfo, caseRef, deceasedTabConfig, deceasedDetailsConfig);
      await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig);
      await basePage.seeUpdatesOnCase(testInfo, caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig);

      nextStepName = 'Grant of probate details';
      endState = 'Application updated';
      await basePage.logInfo(scenarioName, nextStepName, caseRef);
      await cwEventActionsPage.chooseNextStep(nextStepName);
      await solCreateCasePage.grantOfProbatePage1();
      await solCreateCasePage.grantOfProbatePage2(false, true, true);
      await solCreateCasePage.grantOfProbatePage3();
      await solCreateCasePage.grantOfProbatePage4();
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

      await basePage.seeUpdatesOnCase(testInfo, caseRef, applicantDetailsTabConfig, 'ApplicantAndAdditionalExecutorInfo', gopConfig);
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

      await basePage.logInfo(scenarioName, 'Payment', caseRef);
      await solCreateCasePage.makePaymentPage1(caseRef, serviceRequestTabConfig);
      await solCreateCasePage.reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig);
      await solCreateCasePage.makePaymentPage2(caseRef);
      await solCreateCasePage.viewPaymentStatus(testInfo, caseRef);

      await solCreateCasePage.seeEndState(endState);
      await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
      await basePage.seeCaseDetails(testInfo, caseRef, copiesTabConfig, completeApplicationConfig);
      await basePage.seeCaseDetails(testInfo, caseRef, applicantExecutorDetailsTabConfig, gopDtlsAndDcsdDtls);

  });
});
