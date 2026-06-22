import { test } from "../../../Fixtures/fixtures.ts";
import createCaseConfig from "../../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import createGrantOfProbateConfig from "../../../Pages/createGrantOfProbateManual/createGrantOfProbateManualConfig.json" with { type: "json" };
import nextStepConfig from "../../../Pages/nextStep/nextStepConfig.json" with { type: "json" };

const caseTypeMap: { [key: string]: string } = {
  "grantofprobate": "Grant of Probate",
  "intestacy": "Intestacy",
  "admon": "Admon Will",
  "adcolligenda": "Ad Colligenda Bona"
};

const getCaseTypeFromEnv = (): string => {
  const envCaseType = process.env.CASE_TYPE?.toLowerCase() || "grantofprobate";
  return caseTypeMap[envCaseType] || "Grant of Probate";
};

test.describe("Caseworker - Case State Setup", () => {
  let caseRef: string;

  test.beforeEach(async ({ basePage, signInPage, createCasePage }) => {
    test.setTimeout(300000);
    const caseType = getCaseTypeFromEnv();
    const scenarioName = `Caseworker ${caseType} - Case State Setup`;
    const unique_deceased_user = Date.now().toString();

    await basePage.logInfo(scenarioName, "Login as Caseworker", undefined);
    await signInPage.authenticateWithIdamIfAvailable(false);

    const nextStepName = "PA1P/PA1A/Solicitors Manual";
    await basePage.logInfo(scenarioName, nextStepName, undefined);

    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_gor,
      createCaseConfig.list3_text_gor_manual
    );

    await basePage.logInfo(scenarioName, "enterGrantOfProbateManualPage1", undefined);
    const configWithCaseType = { ...createGrantOfProbateConfig, page1_list3_case_type: caseType };
    await createCasePage.enterGrantOfProbateManualPage1(
      "create",
      configWithCaseType,
      unique_deceased_user
    );

    await basePage.logInfo(scenarioName, "enterGrantOfProbateManualPage2", undefined);
    await createCasePage.enterGrantOfProbateManualPage2("create");

    await basePage.logInfo(scenarioName, "enterGrantOfProbateManualPage3", undefined);
    await createCasePage.enterGrantOfProbateManualPage3(
      "create",
      configWithCaseType
    );

    await createCasePage.checkMyAnswers(nextStepName);

    caseRef = await basePage.getCaseRefFromUrl();
    await basePage.logInfo(scenarioName, "Case created", caseRef);
  });

  test.only("Get case to Ready to Issue state", async ({ basePage, solCreateCasePage, cwEventActionsPage }) => {
    const nextStepName = 'Generate grant preview';
    await basePage.logInfo('Caseworker - Case State Setup', nextStepName, caseRef);
    await solCreateCasePage.navigateToCase(caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.generateGrantPreview);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
  });

  test("Get case to Case Stopped state", async () => {
  });

  test("Get case to SME Referral state", async () => {
  });
});
