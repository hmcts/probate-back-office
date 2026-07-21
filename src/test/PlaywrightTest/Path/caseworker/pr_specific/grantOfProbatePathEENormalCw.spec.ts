import dateFns from "date-fns";
import { test } from "../../../Fixtures/fixtures.ts";
import caseMatchesConfig from "../../../Pages/caseMatches/grantOfProbate/caseMatchesConfigEE.json" with { type: "json" };
import createCaseConfig from "../../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import eventSummaryConfig from "../../../Pages/eventSummary/eventSummaryConfig.json" with { type: "json" };
import { testConfig } from "../../../Configs/config.ts";
import applicantDetailsTabConfig from "../../../Pages/caseDetails/grantOfProbate/applicantDetailsTabConfigEE.json" with { type: "json" };
import caseDetailsTabConfig from "../../../Pages/caseDetails/grantOfProbate/caseDetailsTabConfigEE.json" with { type: "json" };
import caseMatchesTabConfig from "../../../Pages/caseDetails/grantOfProbate/caseMatchesTabConfig.json" with { type: "json" };
import copiesTabConfig from "../../../Pages/caseDetails/grantOfProbate/copiesTabConfig.json" with { type: "json" };
import deceasedTabConfig from "../../../Pages/caseDetails/grantOfProbate/deceasedTabConfigEE.json" with { type: "json" };
import documentUploadTabConfig from "../../../Pages/caseDetails/grantOfProbate/documentUploadTabConfig.json" with { type: "json" };
import grantNotificationsTabConfig from "../../../Pages/caseDetails/grantOfProbate/grantNotificationsTabConfig.json" with { type: "json" };
import historyTabConfig from "../../../Pages/caseDetails/grantOfProbate/historyTabConfig.json" with { type: "json" };
import ihtTabConfig from "../../../Pages/caseDetails/grantOfProbate/ihtTabConfig.json" with { type: "json" };
import ihtTabConfigUpdate from "../../../Pages/caseDetails/grantOfProbate/ihtUpdateTabConfig.json" with { type: "json" };
import createGrantOfProbateConfig from "../../../Pages/createGrantOfProbateManual/createGrantOfProbateManualConfig.json" with { type: "json" };
import documentUploadConfig from "../../../Pages/documentUpload/grantOfProbate/documentUploadConfig.json" with { type: "json" };
import issueGrantConfig from "../../../Pages/issueGrant/issueGrantConfig.json" with { type: "json" };
import nextStepConfig from "../../../Pages/nextStep/nextStepConfig.json" with { type: "json" };
import tasks from "../../../Pages/Tasks/tasks.json" with { type: "json" };

test.describe("Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Non Experience Caseworker", () => {
  test("Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Non Experience Caseworker @webkit", async ({
    basePage,
    signInPage,
    createCasePage,
    tasksPage,
    myWorkPage,
    cwEventActionsPage,
    request,
    waEnabled,
  }, testInfo) => {
    test.setTimeout(300000);
    const scenarioName =
      "Caseworker Grant of Representation - Personal application - Grant issued - Expected Estate - Non Experience Caseworker";

    //if waEnabled = true sign in with ctsc admin else use caseworker
    if (waEnabled) {
      // BO Grant of Representation (Personal): Case created -> Grant issued
      await basePage.logInfo(scenarioName, "Login as Ctsc Admin");
      await signInPage.authenticateUserWorkAllocation(
        "CTSC Administrator",
        testConfig.CaseProgressSignInDelay,
      );
    }
    else{
      await signInPage.authenticateWithIdamIfAvailable(false);
    }

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now().toString();

    // FIRST case is only needed for case-matching with SECOND one
    //Using this first case to run WA test
    let nextStepName = "PA1P/PA1A/Solicitors Manual";
    await basePage.logInfo(scenarioName, nextStepName + " - first case", undefined);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_gor,
      createCaseConfig.list3_text_gor_manual,
    );
    await basePage.logInfo(
      scenarioName,
      "enterGrantOfProbateManualPage1",
      undefined,
    );
    await createCasePage.enterGrantOfProbateManualPage1(
      "create",
      createGrantOfProbateConfig,
      unique_deceased_user,
      createGrantOfProbateConfig.page1_deceasedDod_year,
    );
    await basePage.logInfo(
      scenarioName,
      "enterGrantOfProbateManualPage2",
      undefined,
    );
    await createCasePage.enterGrantOfProbateManualPage2("create");
    await basePage.logInfo(
      scenarioName,
      "enterGrantOfProbateManualPage3",
      undefined,
    );
    await createCasePage.enterGrantOfProbateManualPage3(
      "create",
      createGrantOfProbateConfig,
    );
    await createCasePage.checkMyAnswers(nextStepName);
    const workAllocationCaseRef = await basePage.getCaseRefFromUrl();

    if (waEnabled) {
      await basePage.logInfo(
        scenarioName,
        "Confirming the examine digital task is created and visible in the tasks tab",
        workAllocationCaseRef,
      );
      await tasksPage.verifyTaskVisibilty(
        tasks.ExamineDigitalCaseProbate.Name,
        true,
      );
      await tasksPage.verifyUnassignedTaskData(
        tasks.ExamineDigitalCaseProbate.WorkingDaysToComplete,
        tasks.ExamineDigitalCaseProbate.Priority,
        request,
      );
      await basePage.logInfo(
        scenarioName,
        "Assigning the examine digital task to the current user",
        workAllocationCaseRef,
      );
      await tasksPage.assignTaskToSelf("probate ctsc administrator one");
      await tasksPage.verifyAssignedTaskData(false);
      await tasksPage.verifyNextStepsOptions(
        tasks.ExamineDigitalCaseProbate.NextSteps,
      );
      await basePage.logInfo(
        scenarioName,
        "Confirming the task is visible in My Work and can be completed",
        workAllocationCaseRef,
      );
      await basePage.navigateViaUrl("/work/my-work/list");
      await myWorkPage.verifyTaskIsDisplayedOnMyWorkPage(
        tasks.ExamineDigitalCaseProbate.Name,
        tasks.ExamineDigitalCaseProbate.WorkingDaysToComplete,
        tasks.ExamineDigitalCaseProbate.Priority,
        request,
      );
      await myWorkPage.goToTopRowTask();
      await tasksPage.triggerNextStepEvent(
        tasks.ExamineDigitalCaseProbate.NextSteps[2],
      );
      await cwEventActionsPage.caseProgressSelectEscalateReason();
      await cwEventActionsPage.enterEventSummary(
        workAllocationCaseRef,
        "Escalate to registrar",
      );
      await basePage.logInfo(
        scenarioName,
        "Verifying the work allocation task disappears after completion",
        workAllocationCaseRef,
      );
      await tasksPage.verifyTaskVisibilty(
        tasks.ExamineDigitalCaseProbate.Name,
        false,
      );

      await basePage.logInfo(
        scenarioName,
        "Switching back to caseworker context",
        undefined,
      );
      //sign out of WA user
      await signInPage.signOut();

      // SECOND case - the main test case
      await signInPage.authenticateWithIdamIfAvailable(false);
    }

    let endState;
    await basePage.logInfo(scenarioName, nextStepName + " - second case", undefined);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_gor,
      createCaseConfig.list3_text_gor_manual,
    );
    await basePage.logInfo(
      scenarioName,
      "enterGrantOfProbateManualPage1",
      undefined,
    );
    await createCasePage.enterGrantOfProbateManualPage1(
      "create",
      createGrantOfProbateConfig,
      unique_deceased_user,
      createGrantOfProbateConfig.page1_deceasedDod_year,
    );
    await basePage.logInfo(
      scenarioName,
      "enterGrantOfProbateManualPage2",
      undefined,
    );
    await createCasePage.enterGrantOfProbateManualPage2("create");
    await basePage.logInfo(
      scenarioName,
      "enterGrantOfProbateManualPage3",
      undefined,
    );
    await createCasePage.enterGrantOfProbateManualPage3(
      "create",
      createGrantOfProbateConfig,
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
      endState,
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      deceasedTabConfig,
      createGrantOfProbateConfig,
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caseDetailsTabConfig,
      createGrantOfProbateConfig,
    );
    await basePage.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      applicantDetailsTabConfig,
      createGrantOfProbateConfig,
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      copiesTabConfig,
      createGrantOfProbateConfig,
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      ihtTabConfig,
      createGrantOfProbateConfig,
    );

    nextStepName = "Handle supplementary evidence";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.handleSupEvidence);
    await cwEventActionsPage.handleEvidence(caseRef);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState,
    );

    //    await I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, createGrantOfProbateConfig);

    nextStepName = "Add Comment";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.addComment);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState,
    );

    nextStepName = "Upload Documents";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.uploadDocument);
    await cwEventActionsPage.uploadDocument(caseRef, documentUploadConfig);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState,
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      documentUploadTabConfig,
      documentUploadConfig,
    );

    // "reverting" update back to defaults - to enable case-match with matching case
    nextStepName = "Amend case details";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.amendCaseDetails);
    await createCasePage.enterGrantOfProbatePage4("EE");
    await createCasePage.checkMyAnswers(nextStepName);
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      ihtTabConfigUpdate,
      createGrantOfProbateConfig,
    );

    nextStepName = "Stop case";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.stopCase);
    await cwEventActionsPage.caseProgressStopEscalateIssueAddCaseStoppedReason();
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Case stopped";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState,
    );

    nextStepName = "Request information";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.requestInformationPage1(nextStepName);
    await cwEventActionsPage.requestInformationPage2(nextStepName);
    await cwEventActionsPage.requestInformationPage3(nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Case stopped";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState,
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      documentUploadTabConfig,
      documentUploadConfig,
    );

    nextStepName = "Resolve stop";
    const resolveStop = "Ready to issue";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.resolveStop);
    await cwEventActionsPage.chooseResolveStop(resolveStop);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Ready to issue";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState,
    );

    nextStepName = "Find matches (Issue grant)";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(
      nextStepConfig.findMatchesIssueGrant,
    );
    await cwEventActionsPage.selectCaseMatches(caseRef, nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Case Matching (Issue grant)";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState,
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caseMatchesTabConfig,
      caseMatchesConfig,
    );

    nextStepName = "Find matches (cases)";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.findMatch);
    await cwEventActionsPage.selectCaseMatches(caseRef, nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Case Matching (Issue grant)";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState,
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caseMatchesTabConfig,
      caseMatchesConfig,
    );

    nextStepName = "Issue grant";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.issueGrant);
    await cwEventActionsPage.issueGrant(caseRef);
    endState = "Grant issued";
    await basePage.logInfo(testInfo.title, scenarioName, caseRef);

    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);

    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState,
    );
    // When sending an email notification, the Date added for the email notification is set to today
    issueGrantConfig.date = dateFns.format(new Date(), testConfig.dateFormat);
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      grantNotificationsTabConfig,
      issueGrantConfig,
    );
  });
});
