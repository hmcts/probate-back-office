import { test } from "../../Fixtures/fixtures.ts";

import { testConfig } from "../../Configs/config.ts";
import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };

import applicantDetailsTabConfig from "../../Pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE.json" with { type: "json" };
import caseDetailsTabConfig from "../../Pages/caseDetails/grantOfProbate/caseDetailsTabConfigProbateMan.json" with { type: "json" };
import caseMatchesConfig from "../../Pages/caseMatches/grantOfProbate/probateManCaseMatchesConfig.json" with { type: "json" };
import createGrantOfProbateManualProbateManCaseConfig from "../../Pages/createGrantOfProbateManualForProbateMan/createGrantOfProbateManualProbateManCaseConfig.json" with { type: "json" };
import eventSummaryConfig from "../../Pages/eventSummary/eventSummaryConfig.json" with { type: "json" };

import copiesTabConfig from "../../Pages/caseDetails/grantOfProbate/copiesTabConfig.json" with { type: "json" };
import deceasedTabConfig from "../../Pages/caseDetails/grantOfProbate/deceasedTabConfigEE.json" with { type: "json" };
import historyTabConfig from "../../Pages/caseDetails/grantOfProbate/historyTabConfig.json" with { type: "json" };
import caseMatchesTabConfig from "../../Pages/caseDetails/grantOfProbate/probateManCaseMatchesTabConfig.json" with { type: "json" };
import caseProgressConfig from "../../Pages/caseProgressStandard/caseProgressConfig.json" with { type: "json" };

test.describe("Caseworker Grant of Representation - probateman case match", () => {
  test("Caseworker Grant of Representation - probateman case match", async ({
    basePage,
    signInPage,
    createCasePage,
    cwEventActionsPage,
  }, testInfo) => {
    const scenarioName =
      "Caseworker Grant of Representation - probateman case match";
    if (
      testConfig.TestBackOfficeUrl.includes("demo") ||
      testConfig.TestBackOfficeUrl.includes("aat")
    ) {
      let endState;
      await basePage.logInfo(scenarioName, "Login as Caseworker", null);
      await signInPage.authenticateWithIdamIfAvailable(false);

      // Create case with same deceased details from legacy database

      let nextStepName = "PA1P/PA1A/Solicitors Manual";
      await basePage.logInfo(scenarioName, nextStepName + " - first case", null);
      await createCasePage.selectNewCase();
      await createCasePage.selectCaseTypeOptions(
        createCaseConfig.list2_text_gor,
        createCaseConfig.list3_text_gor_manual
      );
      await basePage.logInfo(
        scenarioName,
        "enterGrantOfProbateManualForProbateManPage1",
        null
      );
      await createCasePage.enterGrantOfProbateManualPage1(
        "create",
        createGrantOfProbateManualProbateManCaseConfig
      );
      await basePage.logInfo(
        scenarioName,
        "enterGrantOfProbateManualForProbateManPage2",
        null
      );
      await createCasePage.enterGrantOfProbateManualPage2("createIHT400");
      await createCasePage.enterIhtDetails(
        caseProgressConfig,
        caseProgressConfig.optionYes
      );
      await basePage.logInfo(
        scenarioName,
        "enterGrantOfProbateManualForProbateManPage3", null
      );
      await createCasePage.enterGrantOfProbateManualPage3(
        "create",
        createGrantOfProbateManualProbateManCaseConfig
      );
      await createCasePage.checkMyAnswers(nextStepName);
      endState = "Awaiting documentation";

      const caseRef = await basePage.getCaseRefFromUrl();

      await basePage.logInfo(scenarioName, nextStepName, caseRef);
      await basePage.seeCaseDetails(
        testInfo,
        caseRef,
        historyTabConfig,
        eventSummaryConfig,
        nextStepName,
        endState
      );
      await basePage.seeCaseDetails(
        testInfo,
        caseRef,
        deceasedTabConfig,
        createGrantOfProbateManualProbateManCaseConfig
      );
      await basePage.seeCaseDetails(
        testInfo,
        caseRef,
        caseDetailsTabConfig,
        createGrantOfProbateManualProbateManCaseConfig
      );
      await basePage.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
      await basePage.seeCaseDetails(
        testInfo,
        caseRef,
        applicantDetailsTabConfig,
        createGrantOfProbateManualProbateManCaseConfig
      );
      await basePage.seeCaseDetails(
        testInfo,
        caseRef,
        copiesTabConfig,
        createGrantOfProbateManualProbateManCaseConfig
      );

      nextStepName = "Find matches (cases)";
      await basePage.logInfo(scenarioName, nextStepName, caseRef);
      await cwEventActionsPage.chooseNextStep(nextStepName);
      await cwEventActionsPage.selectProbateManCaseMatchesForGrantOfProbate(
        caseRef,
        nextStepName,
      );
      await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
      endState = "Awaiting documentation";
      await basePage.seeCaseDetails(
        testInfo,
        caseRef,
        historyTabConfig,
        eventSummaryConfig,
        nextStepName,
        endState
      );
      await basePage.seeCaseDetails(
        testInfo,
        caseRef,
        caseMatchesTabConfig,
        caseMatchesConfig
      );
      await cwEventActionsPage.verifyProbateManCcdCaseNumber();
    }
  });
});
