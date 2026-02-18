import dateFns from "date-fns";
import { test } from "../../../Fixtures/fixtures.ts";

import createCaseConfig from "../../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import eventSummaryConfig from "../../../Pages/eventSummary/eventSummaryConfig.json" with { type: "json" };

import caseMatchesTabConfig from "../../../Pages/caseDetails/caveat/caseMatchesTabConfig.json" with { type: "json" };
import caseMatchesConfig from "../../../Pages/caseMatches/caveat/caseMatchesConfig.json" with { type: "json" };
import createCaveatConfig from "../../../Pages/createCaveat/createCaveatConfig.json" with { type: "json" };
import emailCaveatorConfig from "../../../Pages/emailNotifications/caveat/emailCaveatorConfig.json" with { type: "json" };

import documentUploadConfig from "../../../Pages/documentUpload/caveat/documentUploadConfig.json" with { type: "json" };

import historyTabConfig from "../../../Pages/caseDetails/caveat/historyTabConfig.json" with { type: "json" };

import caseDetailsTabConfig from "../../../Pages/caseDetails/caveat/caseDetailsTabConfig.json" with { type: "json" };
import caveatDetailsTabConfig from "../../../Pages/caseDetails/caveat/caveatDetailsTabConfig.json" with { type: "json" };
import caveatorDetailsTabConfig from "../../../Pages/caseDetails/caveat/caveatorDetailsTabConfig.json" with { type: "json" };
import deceasedDetailsTabConfig from "../../../Pages/caseDetails/caveat/deceasedDetailsTabConfig.json" with { type: "json" };

import caseDetailsTabUpdateConfig from "../../../Pages/caseDetails/caveat/caseDetailsTabUpdateConfig.json" with { type: "json" };
import caveatDetailsTabUpdateConfig from "../../../Pages/caseDetails/caveat/caveatDetailsTabUpdateConfig.json" with { type: "json" };
import caveatorDetailsTabUpdateConfig from "../../../Pages/caseDetails/caveat/caveatorDetailsTabUpdateConfig.json" with { type: "json" };
import deceasedDetailsTabUpdateConfig from "../../../Pages/caseDetails/caveat/deceasedDetailsTabUpdateConfig.json" with { type: "json" };

import documentsTabEmailCaveatorConfig from "../../../Pages/caseDetails/caveat/documentsTabEmailCaveatorConfig.json" with { type: "json" };
import documentsTabUploadDocumentConfig from "../../../Pages/caseDetails/caveat/documentsTabUploadDocumentConfig.json" with { type: "json" };

import registrarsDecisionConfig from "../../../Pages/caseDetails/caveat/registrarsDecisionConfig.json" with { type: "json" };
import registrarsDecisionTabConfig from "../../../Pages/caseDetails/caveat/registrarsDecisionTabConfig.json" with { type: "json" };

import { testConfig } from "../../../Configs/config.ts";

test.describe("Caseworker Caveat1 - Order summons", () => {
  test("Caseworker Caveat1 - Order summons @galaxys4 @ipadpro11", async ({
    basePage,
    signInPage,
    createCasePage,
    cwEventActionsPage,
  }, testInfo) => {
    test.setTimeout(300000);
    const scenarioName = "Caseworker Caveat1 - Order summons";
    // BO Caveat (Personal): Raise a caveat -> Caveat not matched -> Order summons

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now().toString();

    await basePage.logInfo(scenarioName, "Login as Caseworker", null);
    await signInPage.authenticateWithIdamIfAvailable(false);

    // FIRST case is only needed for case-matching with SECOND one

    let nextStepName = "Raise a caveat";
    await basePage.logInfo(scenarioName, nextStepName, null);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_caveat,
      createCaseConfig.list3_text_caveat
    );
    await createCasePage.enterCaveatPage1("create");
    await createCasePage.enterCaveatPage2("create", unique_deceased_user);
    await createCasePage.enterCaveatPage3("create");
    await createCasePage.enterCaveatPage4("create");
    await createCasePage.checkMyAnswers(nextStepName);

    // SECOND case - the main test case

    await basePage.logInfo(scenarioName, nextStepName, null);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_caveat,
      createCaseConfig.list3_text_caveat
    );
    await createCasePage.enterCaveatPage1("create");
    await createCasePage.enterCaveatPage2("create", unique_deceased_user);
    await createCasePage.enterCaveatPage3("create");
    await createCasePage.enterCaveatPage4("create");
    await createCasePage.checkMyAnswers(nextStepName);
    let endState = "Caveat raised";

    const caseRef = await basePage.getCaseRefFromUrl();

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
      caseDetailsTabConfig,
      createCaveatConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      deceasedDetailsTabConfig,
      createCaveatConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caveatorDetailsTabConfig,
      createCaveatConfig
    );

    // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
    createCaveatConfig.caveat_expiry_date = dateFns.format(
      dateFns.addMonths(new Date(), 6),
      testConfig.dateFormat
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caveatDetailsTabConfig,
      createCaveatConfig
    );

    nextStepName = "Registrar's decision";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.registrarsDecision(caseRef);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      registrarsDecisionTabConfig,
      registrarsDecisionConfig
    );

    nextStepName = "Email caveator"; // When in state 'Caveat raised'
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.emailCaveator(caseRef);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when emailing the caveator.
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );
    // When emailing the caveator, the Date added for the email document is set to today
    emailCaveatorConfig.dateAdded = dateFns.format(
      new Date(),
      testConfig.dateFormat
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      documentsTabEmailCaveatorConfig,
      emailCaveatorConfig
    );

    nextStepName = "Caveat match";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.selectCaseMatches(
      caseRef,
      nextStepName,
      true,
      caseMatchesConfig.addNewButton
    );
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Caveat matching";
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

    nextStepName = "Email caveator"; // When in state 'Caveat closed'
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.emailCaveator(caseRef);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when emailing the caveator.
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );
    // When emailing the caveator, the Date added for the email document is set to today
    emailCaveatorConfig.dateAdded = dateFns.format(
      new Date(),
      testConfig.dateFormat
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      documentsTabEmailCaveatorConfig,
      emailCaveatorConfig
    );

    nextStepName = "Caveat not matched";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Caveat not matched";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );

    nextStepName = "Upload document";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.uploadDocument(caseRef, documentUploadConfig);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when uploading a document.
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
      documentsTabUploadDocumentConfig,
      documentUploadConfig
    );

    nextStepName = "Add comment";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when adding a comment.
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );

    nextStepName = "Await caveat resolution";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Awaiting caveat resolution";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );

    nextStepName = "Warning requested";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Warning validation";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );

    nextStepName = "Issue caveat warning";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Awaiting warning response";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );

    nextStepName = "Order summons";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Summons ordered";
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );

    nextStepName = "Amend caveat details";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await createCasePage.enterCaveatPage1("update");
    await createCasePage.enterCaveatPage2("update", unique_deceased_user);
    await createCasePage.enterCaveatPage3("update");
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);

    // Note that End State does not change when amending the caveat details.
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
      caseDetailsTabUpdateConfig,
      createCaveatConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      deceasedDetailsTabUpdateConfig,
      createCaveatConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caveatorDetailsTabUpdateConfig,
      createCaveatConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      caveatDetailsTabUpdateConfig,
      createCaveatConfig
    );

    nextStepName = "Withdraw caveat";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.withdrawCaveatPage1();
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Caveat closed";
    await basePage.logInfo(scenarioName, endState, null);
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );

    await signInPage.signOut();
  });
});
