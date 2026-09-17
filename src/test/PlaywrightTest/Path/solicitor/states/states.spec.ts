import { test } from "../../../Fixtures/fixtures.ts";
import { createSolicitorCase } from "../case creation/solicitorCaseCreationHelper.ts";
import nextStepConfig from "../../../Pages/nextStep/nextStepConfig.json" with { type: "json" };

test.describe("Solicitor - Case State Setup", () => {
  let caseRef: string;

  test.beforeEach(async ({ basePage, signInPage, createCasePage, solCreateCasePage, cwEventActionsPage }) => {
    test.setTimeout(300000);
    const caseTypeKey = (process.env.CASE_TYPE ?? 'probate').toLowerCase();

    caseRef = await createSolicitorCase(
      { basePage, signInPage, createCasePage, solCreateCasePage, cwEventActionsPage },
      caseTypeKey
    );

    await basePage.logInfo('Solicitor - Case State Setup', 'Case reached Awaiting documentation, signing out', caseRef);
    await signInPage.signOut();

    await basePage.logInfo('Solicitor - Case State Setup', 'Login as Caseworker', caseRef);
    await signInPage.authenticateWithIdamIfAvailable(false);
    await solCreateCasePage.navigateToCase(caseRef);
  });

  test("Get case to Ready to Issue state", async ({ basePage, solCreateCasePage, cwEventActionsPage }) => {
    const nextStepName = 'Generate grant preview';
    await basePage.logInfo('Solicitor - Case State Setup', nextStepName, caseRef);
    await solCreateCasePage.navigateToCase(caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.generateGrantPreview);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
  });

  test("Get case to Case Stopped state", async ({ basePage, solCreateCasePage, cwEventActionsPage }) => {
    const nextStepName = 'Stop case';
    await basePage.logInfo('Solicitor - Case State Setup', nextStepName, caseRef);
    await solCreateCasePage.navigateToCase(caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.stopCase);
    await cwEventActionsPage.caseProgressStopEscalateIssueAddCaseStoppedReason();
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
  });

  test("Get case to SME Referral state", async ({ basePage, solCreateCasePage, cwEventActionsPage }) => {
    const nextStepName = 'SME Referral';
    await basePage.logInfo('Solicitor - Case State Setup', nextStepName, caseRef);
    await solCreateCasePage.navigateToCase(caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.smeReferral);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
  });
});
