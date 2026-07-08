import { test } from "../../Fixtures/fixtures.ts";
import { testConfig } from "../../Configs/config.ts";

test.describe("Work Allocation - Examine Digital Case - Probate Task", () => {
  test("Create task via manual, assign and complete a probate task via escalate to registrar event @webkit", async ({
    basePage,
    signInPage,
    createCasePage,
  }) => {
    const scenarioName =
      "Work Allocation - Examine Digital Case - Probate Task";

    // Create Examine Digital Case - Probate task using PA1P/PA1A/Solicitors Manual
    basePage.logInfo(scenarioName, "Login as Ctsc Admin");
    await signInPage.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
   
    // Verify that the task is visible on the Available Work tab? (Come back once everything else is done to determine complexity)
    // Verify the task is visible in the tasks tab on the case
    // Assign the task to yourself
    // Verify that the task is visible in the My Work tab? (Come back once everything else is done to determine complexity)
    // Verify the details of the task (i.e priority, name, etc.), look at criteria 2 on AC
    // Complete the task using Escalate to registrar event link
    // Verify the task disappears from the UI
  });
});
