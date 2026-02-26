import { test } from "../../Fixtures/fixtures.ts";

import dateFns from "date-fns";

import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import eventSummaryConfig from "../../Pages/eventSummary/eventSummaryConfig.json" with { type: "json" };

import caseMatchesConfig from "../../Pages/caseMatches/willLodgement/caseMatchesConfig.json" with { type: "json" };
import createWillLodgementConfig from "../../Pages/createWillLodgement/createWillLodgementConfig.json" with { type: "json" };
import documentUploadConfig from "../../Pages/documentUpload/willLodgement/documentUploadConfig.json" with { type: "json" };
import generateDepositReceiptConfig from "../../Pages/generateDepositReceipt/generateDepositReceiptConfig.json" with { type: "json" };
import withdrawWillConfig from "../../Pages/withdrawal/willLodgement/withdrawalConfig.json" with { type: "json" };

import historyTabConfig from "../../Pages/caseDetails/willLodgement/historyTabConfig.json" with { type: "json" };

import caseDetailsTabConfig from "../../Pages/caseDetails/willLodgement/caseDetailsTabConfig.json" with { type: "json" };
import executorTabConfig from "../../Pages/caseDetails/willLodgement/executorTabConfig.json" with { type: "json" };
import testatorTabConfig from "../../Pages/caseDetails/willLodgement/testatorTabConfig.json" with { type: "json" };

import caseDetailsTabUpdateConfig from "../../Pages/caseDetails/willLodgement/caseDetailsTabUpdateConfig.json" with { type: "json" };
import executorTabUpdateConfig from "../../Pages/caseDetails/willLodgement/executorTabUpdateConfig.json" with { type: "json" };
import testatorTabUpdateConfig from "../../Pages/caseDetails/willLodgement/testatorTabUpdateConfig.json" with { type: "json" };

import documentsTabGenerateDepositReceiptConfig from "../../Pages/caseDetails/willLodgement/documentsTabGenerateDepositReceiptConfig.json" with { type: "json" };
import documentsTabUploadDocumentConfig from "../../Pages/caseDetails/willLodgement/documentsTabUploadDocumentConfig.json" with { type: "json" };

import { testConfig } from "../../Configs/config.ts";
import caseMatchesTabConfig from "../../Pages/caseDetails/willLodgement/caseMatchesTabConfig.json" with { type: "json" };
import willWithdrawalDetailsTabConfig from "../../Pages/caseDetails/willLodgement/willWithdrawalDetailsTabConfig.json" with { type: "json" };

test.describe("Caseworker Will Lodgement - Withdraw will", () => {
  test("Caseworker Will Lodgement - Withdraw will @galaxys4", async ({
    basePage,
    signInPage,
    createCasePage,
    cwEventActionsPage,
  }, testInfo) => {
    const scenarioName = "Caseworker Will Lodgement - Withdraw will";

    // BO Will Lodgement (Personal): Create a will lodgement -> Withdraw will

    // get unique suffix for names - in order to match only against 1 case
    const unique_deceased_user = Date.now().toString();

    await basePage.logInfo(scenarioName, "Login as Caseworker", null);
    await signInPage.authenticateWithIdamIfAvailable(false);

    // FIRST case is only needed for case-matching with SECOND one

    let nextStepName = "Create a will lodgement";
    await basePage.logInfo(scenarioName, nextStepName, null);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_will,
      createCaseConfig.list3_text_will
    );
    await createCasePage.enterWillLodgementPage1("create");
    await createCasePage.enterWillLodgementPage2(
      "create",
      unique_deceased_user
    );
    await createCasePage.enterWillLodgementPage3("create");
    await createCasePage.checkMyAnswers(nextStepName);

    // SECOND case - the main test case

    await basePage.logInfo(scenarioName, nextStepName, null);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_will,
      createCaseConfig.list3_text_will
    );
    await createCasePage.enterWillLodgementPage1("create");
    await createCasePage.enterWillLodgementPage2(
      "create",
      unique_deceased_user
    );
    await createCasePage.enterWillLodgementPage3("create");
    await createCasePage.checkMyAnswers(nextStepName);
    let endState = "Will lodgement created";

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
      createWillLodgementConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      testatorTabConfig,
      createWillLodgementConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      executorTabConfig,
      createWillLodgementConfig
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
    await cwEventActionsPage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );

    nextStepName = "Amend will lodgement";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await createCasePage.enterWillLodgementPage1("update");
    await createCasePage.enterWillLodgementPage2(
      "update",
      unique_deceased_user
    );
    await createCasePage.enterWillLodgementPage3("update");
    await createCasePage.checkMyAnswers(nextStepName);
    // Note that End State does not change when amending a Will Lodgement.
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
      createWillLodgementConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      testatorTabUpdateConfig,
      createWillLodgementConfig
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      executorTabUpdateConfig,
      createWillLodgementConfig
    );

    nextStepName = "Generate deposit receipt";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    // Note that End State does not change when generating a deposit receipt.
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      historyTabConfig,
      eventSummaryConfig,
      nextStepName,
      endState
    );
    // When generating a deposit receipt, the Date added for the deposit receipt document is set to today
    generateDepositReceiptConfig.dateAdded = dateFns.format(
      new Date(),
      testConfig.dateFormat
    );
    await basePage.seeCaseDetails(
      testInfo,
      caseRef,
      documentsTabGenerateDepositReceiptConfig,
      generateDepositReceiptConfig
    );

    // "reverting" update back to defaults - to enable case-match with matching case
    nextStepName = "Amend will lodgement";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await createCasePage.enterWillLodgementPage2("update2orig");
    await createCasePage.checkMyAnswers(nextStepName);

    nextStepName = "Match application";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.selectCaseMatches(caseRef, nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Will lodged";
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

    nextStepName = "Withdraw will";
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await cwEventActionsPage.selectWithdrawalReason(
      caseRef,
      withdrawWillConfig
    );
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    endState = "Will withdrawn";
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
      willWithdrawalDetailsTabConfig,
      withdrawWillConfig
    );
    await basePage.logInfo(scenarioName, endState, caseRef);

    await signInPage.signOut();
  });
});
