import dateFns from "date-fns";
import { test } from "../../Fixtures/fixtures.ts";

// const {runAccessibilityTest} = require('../../Accessibility/axeUtils');
// const testConfig = require('src/test/config');
import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };

import applicationDetailsConfig from "../../Pages/solicitorApplyCaveat/applicationDetails/applicationDetails.json" with { type: "json" };
import applyCaveatConfig from "../../Pages/solicitorApplyCaveat/applyCaveat/applyCaveat.json" with { type: "json" };
import completeApplicationConfig from "../../Pages/solicitorApplyCaveat/completeApplication/completeApplication.json" with { type: "json" };

import { testConfig } from "../../Configs/config.ts";
import caseDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyCaveat/caseDetailsTabConfig.json" with { type: "json" };
import caveatDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyCaveat/caveatDetailsTabConfig.json" with { type: "json" };
import caveatorDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyCaveat/caveatorDetailsTabConfig.json" with { type: "json" };
import deceasedDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyCaveat/deceasedDetailsTabConfig.json" with { type: "json" };
import historyTabConfig from "../../Pages/caseDetails/solicitorApplyCaveat/historyTabConfig.json" with { type: "json" };
import notificationsTabConfig from "../../Pages/caseDetails/solicitorApplyCaveat/notificationsTabConfig.json" with { type: "json" };
import serviceRequestReviewTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig.json" with { type: "json" };
import serviceRequestTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig.json" with { type: "json" };

test.describe("Solicitor - Apply Caveat", () => {
  test("Solicitor - Apply Caveat", async ({
    basePage,
    signInPage,
    createCasePage,
    solCreateCasePage,
    cwEventActionsPage,
  }, testInfo) => {
    const scenarioName = "Solicitor - Apply Caveat";

    await basePage.logInfo(scenarioName, "Login as Solicitor", null);
    await signInPage.authenticateWithIdamIfAvailable(true);

    let nextStepName = "Application details";
    let endState = "Caveat created";
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_caveat,
      createCaseConfig.list4_text_caveat
    );
    await solCreateCasePage.applyCaveatPage1();
    await solCreateCasePage.applyCaveatPage2();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    const caseRef = await basePage.getCaseRefFromUrl();

    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      {},
      createCaseConfig.list3_text_caveat,
      endState
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caseDetailsTabConfig,
      applyCaveatConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caveatorDetailsTabConfig,
      applyCaveatConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caveatDetailsTabConfig,
      applyCaveatConfig
    );

    endState = "Caveat updated";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.caveatApplicationDetailsPage1();
    await solCreateCasePage.caveatApplicationDetailsPage2();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      deceasedDetailsTabConfig,
      applicationDetailsConfig
    );
    await basePage.seeUpdatesOnCase(
      testInfo,
      caseRef,
      caveatorDetailsTabConfig,
      "caveatorApplicationDetails",
      applicationDetailsConfig
    );

    nextStepName = "Submit application";
    endState = "Caveat raised";
    const applicationType = "Caveat";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.completeCaveatApplicationPage1();
    await solCreateCasePage.completeCaveatApplicationPage2(caseRef);
    // await I.completeCaveatApplicationPage3();

    await basePage.logInfo(scenarioName, "Payment", null);
    await solCreateCasePage.makeCaveatPaymentPage1(
      caseRef,
      serviceRequestTabConfig
    );
    await solCreateCasePage.reviewPaymentDetails(
      caseRef,
      serviceRequestReviewTabConfig
    );
    await solCreateCasePage.makePaymentPage2(caseRef);
    await solCreateCasePage.viewPaymentStatus(
      testInfo,
      caseRef,
      applicationType
    );

    await solCreateCasePage.seeEndState(endState);

    // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
    completeApplicationConfig.caveat_expiry_date = dateFns.format(
      dateFns.addMonths(new Date(), 6),
      testConfig.dateFormat
    );
    // When emailing the caveator, the Date added for the email document is set to today
    completeApplicationConfig.notification_date = dateFns.format(
      new Date(),
      testConfig.dateFormat
    );

    //await I.seeCaseDetails(caseRef, paymentDetailsTabConfig, completeApplicationConfig);
    await basePage.seeUpdatesOnCase(
      testInfo,
      caseRef,
      caveatDetailsTabConfig,
      "completedApplication",
      completeApplicationConfig
    );
    await basePage.seeUpdatesOnCase(
      testInfo,
      caseRef,
      notificationsTabConfig,
      "completedApplication",
      completeApplicationConfig
    );
  });
});
