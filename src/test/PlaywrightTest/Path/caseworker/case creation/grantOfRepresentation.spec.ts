import { test } from "../../../Fixtures/fixtures.ts";
import { testConfig } from "../../../Configs/config.ts";
import createCaseConfig from "../../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import createGrantOfProbateConfig from "../../../Pages/createGrantOfProbateManual/createGrantOfProbateManualConfig.json" with { type: "json" };
import eventSummaryConfig from "../../../Pages/eventSummary/eventSummaryConfig.json" with { type: "json" };

import applicantDetailsTabConfig from "../../../Pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE.json" with { type: "json" };
import caseDetailsTabConfig from "../../../Pages/caseDetails/grantOfProbate/caseDetailsTabConfig.json" with { type: "json" };
import copiesTabConfig from "../../../Pages/caseDetails/grantOfProbate/copiesTabConfig.json" with { type: "json" };
import deceasedTabConfig from "../../../Pages/caseDetails/grantOfProbate/deceasedTabConfig.json" with { type: "json" };
import historyTabConfig from "../../../Pages/caseDetails/grantOfProbate/historyTabConfig.json" with { type: "json" };

const caseTypeMap: { [key: string]: string } = {
  "intestacy": "Intestacy",
  "admon": "Admon Will",
  "adcolligenda": "Ad Colligenda Bona",
  "grantofprobate": "Grant of Probate"
};

const getCaseTypeFromEnv = (): string => {
  const envCaseType = process.env.CASE_TYPE?.toLowerCase() || "grantofprobate";
  return caseTypeMap[envCaseType] || "Grant of Probate";
};

test.describe("Caseworker Grant of Probate - PA1P/PA1A/Solicitors Manual Case Creation", () => {
  test(`Caseworker ${getCaseTypeFromEnv()} - PA1P/PA1A/Solicitors Manual Case Creation @webkit`, async ({
    basePage,
    signInPage,
    createCasePage,
  }, testInfo) => {
    test.setTimeout(300000);
    const caseType = getCaseTypeFromEnv();
    const scenarioName = `Caseworker ${caseType} - PA1P/PA1A/Solicitors Manual Case Creation`;
    const unique_deceased_user = Date.now().toString();

    await basePage.logInfo(scenarioName, "Login as Caseworker", null);
    await signInPage.authenticateWithIdamIfAvailable(false);

    let nextStepName = "PA1P/PA1A/Solicitors Manual";
    await basePage.logInfo(scenarioName, nextStepName, null);

    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_gor,
      createCaseConfig.list3_text_gor_manual
    );

    await basePage.logInfo(scenarioName, "enterGrantOfProbateManualPage1", null);
    const configWithCaseType = { ...createGrantOfProbateConfig, page1_list3_case_type: caseType };
    await createCasePage.enterGrantOfProbateManualPage1(
      "create",
      configWithCaseType,
      unique_deceased_user
    );

    await basePage.logInfo(scenarioName, "enterGrantOfProbateManualPage2", null);
    await createCasePage.enterGrantOfProbateManualPage2("create");

    await basePage.logInfo(scenarioName, "enterGrantOfProbateManualPage3", null);
    await createCasePage.enterGrantOfProbateManualPage3(
      "create",
      createGrantOfProbateConfig
    );

    await createCasePage.checkMyAnswers(nextStepName);

    const caseRef = await basePage.getCaseRefFromUrl();
    const endState = "Awaiting documentation";

    await basePage.logInfo(scenarioName, "Verify case created", caseRef);
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
      createGrantOfProbateConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caseDetailsTabConfig,
      createGrantOfProbateConfig
    );
    await basePage.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      applicantDetailsTabConfig,
      createGrantOfProbateConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      copiesTabConfig,
      createGrantOfProbateConfig
    );
  })
});
