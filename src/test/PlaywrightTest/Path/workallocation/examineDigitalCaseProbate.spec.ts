import { test } from "../../Fixtures/fixtures.ts";
import { testConfig } from "../../Configs/config.ts";
import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import createGrantOfProbateConfig from "../../Pages/createGrantOfProbateManual/createGrantOfProbateManualConfig.json" with { type: "json" };
import caseProgressConfig from "../../Pages/caseProgressStandard/caseProgressConfig.json" with { type: "json" };
import tasks from "../../Pages/Tasks/tasks.json" with { type: "json" };

test.describe("Work Allocation - Examine Digital Case - Probate Task", () => {
  test("Create task via manual, assign and complete a probate task via escalate to registrar event @webkit", async ({
    basePage,
    signInPage,
    createCasePage,
    tasksPage,
    myWorkPage,
    cwEventActionsPage,
  }) => {
    const scenarioName =
      "Work Allocation - Examine Digital Case - Probate Task";

    const unique_deceased_user = Date.now().toString();

    // Create Examine Digital Case - Probate task using PA1P/PA1A/Solicitors Manual
    basePage.logInfo(scenarioName, "Login as Ctsc Admin");
    await signInPage.authenticateUserWorkAllocation(
      "CTSC Administrator",
      testConfig.CaseProgressSignInDelay,
    );

    let nextStepName = "Create PA1P/PA1A/Solicitors Manual case";

    await basePage.logInfo(scenarioName, nextStepName, null);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_gor,
      createCaseConfig.list3_text_gor_manual,
    );
    await basePage.logInfo(
      scenarioName,
      "enterGrantOfProbateManualPage1",
      null,
    );
    await createCasePage.enterGrantOfProbateManualPage1(
      "create",
      createGrantOfProbateConfig,
      unique_deceased_user,
      createGrantOfProbateConfig.page1_deceasedDod_year_update,
    );
    await basePage.logInfo(
      scenarioName,
      "enterGrantOfProbateManualPage2",
      null,
    );
    await createCasePage.enterGrantOfProbateManualPage2("createIHT400");
    await createCasePage.enterIhtDetails(
      caseProgressConfig,
      caseProgressConfig.optionYes,
    );
    await basePage.logInfo(
      scenarioName,
      "enterGrantOfProbateManualPage3",
      null,
    );
    await createCasePage.enterGrantOfProbateManualPage3(
      "create",
      createGrantOfProbateConfig,
    );
    await createCasePage.checkMyAnswers(nextStepName);
    const caseRef = await basePage.getCaseRefFromUrl();
    // Verify that the task is visible on the Available Work tab? (Come back once everything else is done to determine complexity)
    // Verify the task is visible in the tasks tab on the case
    await basePage.logInfo(
      scenarioName,
      "Confirming task is created and visible in the tasks tab on the case",
      caseRef,
    );
    await tasksPage.verifyTaskIsDisplayed(tasks.ExamineDigitalCaseProbate.Name);
    await tasksPage.verifyUnassignedTaskData(
      tasks.ExamineDigitalCaseProbate.WorkingDaysToComplete,
      tasks.ExamineDigitalCaseProbate.Priority,
    );
    // Assign the task to yourself - verify data in task tab
    await basePage.logInfo(
      scenarioName,
      "Assigning task to self and verifying data in the tasks tab",
      caseRef,
    );
    await tasksPage.assignTaskToSelf("probate ctsc administrator one");
    await tasksPage.verifyAssignedTaskData(false);
    await tasksPage.verifyNextStepsOptions(
      tasks.ExamineDigitalCaseProbate.NextSteps,
    );
    // Verify that the task is visible in the My Work tab? (Come back once everything else is done to determine complexity)
    // Verify the details of the task (i.e priority, name, etc.), look at criteria 2 on AC
    await basePage.logInfo(
      scenarioName,
      "Confirming task is visible in the My Work tab and verifying details",
      caseRef,
    );
    await basePage.navigateViaUrl("/work/my-work/list");
    await myWorkPage.verifyTaskIsDisplayedOnMyWorkPage(
      tasks.ExamineDigitalCaseProbate.Name,
      tasks.ExamineDigitalCaseProbate.WorkingDaysToComplete,
      tasks.ExamineDigitalCaseProbate.Priority,
    );
    // Complete the task using Escalate to registrar event link
    await basePage.logInfo(
      scenarioName,
      "Completing the task using Escalate to registrar Next Step link",
      caseRef,
    );
    await myWorkPage.goToTopRowTask();
    await tasksPage.triggerNextStepEvent(
      tasks.ExamineDigitalCaseProbate.NextSteps[2],
    );
    await cwEventActionsPage.caseProgressSelectEscalateReason();
    await cwEventActionsPage.enterEventSummary(
      caseRef,
      "Escalate to registrar",
    );
    // Verify the task disappears from the UI
    await basePage.logInfo(
      scenarioName,
      "Verifying the task disappears from the UI after completion",
      caseRef,
    );
    await tasksPage.verifyTaskIsHidden(tasks.ExamineDigitalCaseProbate.Name);
  });
});
