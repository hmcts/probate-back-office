import dateFns from "date-fns";
import { test } from "../../Fixtures/fixtures.ts";

import { testConfig } from "../../Configs/config.ts";
import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };

import applyProbateConfig from "../../Pages/solicitorApplyProbate/applyProbate/applyProbateConfig.json" with { type: "json" };
import deceasedDetailsConfig from "../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };
import gopConfig from "../../Pages/solicitorApplyProbate/grantOfProbate/grantOfProbate.json" with { type: "json" };
import completeApplicationConfig from "../../Pages/solicitorApplyProbate/completeApplication/completeApplication.json" with { type: "json" };

import applicantDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig.json" with { type: "json" };
import applicantExecutorDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/applicantExecDetailsTrustCorpTabConfig.json" with { type: "json" };
import deceasedTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/deceasedTabConfig.json" with { type: "json" };
import caseDetailsTabDeceasedDtlsConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfigMulti.json" with { type: "json" };
import caseDetailsTabGopConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabGopTrustCorpConfig.json" with { type: "json" };
import caseDetailsTabUpdatesConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig.json" with { type: "json" };
import sotTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/sotTabConfig.json" with { type: "json" };
import copiesTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/copiesTabConfig.json" with { type: "json" };
import historyTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/historyTabConfig.json" with { type: "json" };
import serviceRequestTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig.json" with { type: "json" };
import serviceRequestReviewTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig.json" with { type: "json" };

test.describe("Solicitor - Apply Grant of probate", () => {
    test("Solicitor - Apply Grant of probate Multi Executor", async ({
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
  await I.grantOfProbatePage5();
  await I.grantOfProbatePage6();
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

  });
});
