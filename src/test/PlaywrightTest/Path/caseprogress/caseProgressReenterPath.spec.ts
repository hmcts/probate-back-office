import { test } from "../../Fixtures/fixtures.ts";

import { testConfig } from "../../Configs/config.ts";
import commonConfig from "../../Pages/common/commonConfig.json" with { type: "json" };
import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import caseProgressConfig from "../../Pages/caseProgressStandard/caseProgressConfig.json" with { type: "json" };
import serviceRequestTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig.json" with { type: "json" };
import serviceRequestReviewTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig.json" with { type: "json" };
import documentUploadSolTabConfigBilingual from "../../Pages/caseDetails/grantOfProbate/documentUploadSolTabConfigBilingual.json" with { type: "json" };
import deceasedDetailsConfig from "../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };
import nextStepConfig from "../../Pages/nextStep/nextStepConfig.json" with { type: "json" };

test.describe("03 BO Case Progress E2E - Renter Deceased details", () => {
  test("03 BO Case Progress E2E - Renter Deceased details @edge", async ({
      basePage,
      signInPage,
      createCasePage,
      solCreateCasePage,
      cwEventActionsPage,
      caseProgressPage
    }) => {
    test.setTimeout(600000);
    const scenarioName ='Case Progress - Reenter Deceased Details';

    const isSolicitorNamedExecutor = true;
    const isSolicitorApplyingExecutor = true;
    await basePage.logInfo(scenarioName, 'Login as Solicitor');

    await signInPage.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);

    await basePage.logInfo(scenarioName, 'Initial application entry');
    await solCreateCasePage.applyForProbatePage1();
    await solCreateCasePage.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await solCreateCasePage.cyaPage();
    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 1,
      numInProgress: 0,
      numNotStarted: 1,
      linkText: 'Add deceased details',
      linkUrl: '/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1',
      goToNextStep: true});

    await basePage.logInfo(scenarioName, 'Deceased details');
    await solCreateCasePage.deceasedDetailsPage1();
    await solCreateCasePage.deceasedDetailsPage2();
    await solCreateCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
    await solCreateCasePage.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT400');
    await solCreateCasePage.deceasedDetailsPage3();
    await solCreateCasePage.deceasedDetailsPage4();
    await solCreateCasePage.cyaPage();
    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 2,
      numInProgress: 0,
      numNotStarted: 1,
      linkText: 'Add application details',
      linkUrl: '/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1',
      goToNextStep: true});

    await basePage.logInfo(scenarioName, 'Add application details');
    await solCreateCasePage.grantOfProbatePage1();
    await solCreateCasePage.grantOfProbatePage2(true, isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await solCreateCasePage.grantOfProbatePage3();
    await solCreateCasePage.grantOfProbatePage4(isSolicitorApplyingExecutor);
    await solCreateCasePage.grantOfProbatePage5();
    await solCreateCasePage.grantOfProbatePage6();
    await solCreateCasePage.cyaPage();
    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 3,
      numInProgress: 0,
      numNotStarted: 1,
      linkText: 'Review and sign legal statement and submit application',
      linkUrl: '/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1',
      goToNextStep: true});

    await basePage.logInfo(scenarioName, 'Reenter solicitor details');
    await solCreateCasePage.completeApplicationUpdateSot();
    await solCreateCasePage.selectReenterTask('1: SolAppCreatedSolicitorDtls');
    await caseProgressPage.caseProgressContinueWithoutChangingAnything();
    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 0,
      numInProgress: 4,
      numNotStarted: 0,
      linkText: 'Add Probate practitioner details',
      linkUrl: '/trigger/solicitorUpdateAppSolDtls/solicitorUpdateAppSolDtlssolicitorUpdateAppSolDtlsPage1',
      goToNextStep: true});

    await caseProgressPage.caseProgressContinueWithoutChangingAnything(3);

    await caseProgressPage.caseProgressSelectPenultimateNextStepAndGo();
    await caseProgressPage.caseProgressContinueWithoutChangingAnything(7);

    await caseProgressPage.caseProgressSelectPenultimateNextStepAndGo();
    await caseProgressPage.caseProgressContinueWithoutChangingAnything(6);

    await basePage.waitForNavigationToComplete(commonConfig.submitButton);

    await caseProgressPage.caseProgressSelectPenultimateNextStepAndGo();

    await basePage.logInfo(scenarioName, 'Reenter deceased details');
    await solCreateCasePage.completeApplicationUpdateSot();
    await solCreateCasePage.selectReenterTask('2: SolAppCreatedDeceasedDtls');
    await caseProgressPage.caseProgressContinueWithoutChangingAnything();
    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 1,
      numInProgress: 3,
      numNotStarted: 0,
      linkText: 'Add deceased details',
      linkUrl: '/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1',
      goToNextStep: true});

    await caseProgressPage.caseProgressContinueWithoutChangingAnything(7);

    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 2,
      numInProgress: 2,
      numNotStarted: 0,
      linkText: 'Add application details',
      linkUrl: '/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1',
      goToNextStep: true});

    await caseProgressPage.caseProgressContinueWithoutChangingAnything(6);

    await basePage.waitForNavigationToComplete(commonConfig.submitButton);

    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 3,
      numInProgress: 1,
      numNotStarted: 0,
      linkText: 'Review and sign legal statement and submit application',
      linkUrl: '/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1',
      goToNextStep: true});

    await basePage.logInfo(scenarioName, 'Confirm application');
    await solCreateCasePage.completeApplicationPage1();
    await solCreateCasePage.completeApplicationPage3();
    await solCreateCasePage.completeApplicationPage4();
    await solCreateCasePage.completeApplicationPage5();
    await solCreateCasePage.completeApplicationPage6();
    await solCreateCasePage.completeApplicationPage7();
    await solCreateCasePage.completeApplicationPage8();

    const caseRef = await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 4,
      numInProgress: 0,
      numNotStarted: 1,
      linkText: 'Make payment',
      linkUrl: '#Service%20Request'});

    await basePage.logInfo(scenarioName, 'Payment');
    await solCreateCasePage.makePaymentPage1(caseRef, serviceRequestTabConfig);
    await solCreateCasePage.reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig);
    // await solCreateCasePage.makePaymentPage2(caseRef);
    await solCreateCasePage.makeCardPaymentPage(caseRef);
    await solCreateCasePage.makeCardPaymentPage2();
    await solCreateCasePage.confirmCardDetails();
    await solCreateCasePage.viewCardPaymentStatus(caseRef);

    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 5,
      numInProgress: 1,
      numNotStarted: 0,
      signOut: true});

    let nextStepName = 'Select for QA';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    // log in as case worker
    await signInPage.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.selectForQa);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    await signInPage.signOut();

    await basePage.logInfo(scenarioName, 'Check progress tab for Select for QA', caseRef);
    // log back in as solicitor
    await signInPage.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);
    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 7,
      numInProgress: 1,
      numNotStarted: 0,
      signOut: true});

    nextStepName = 'Generate grant preview';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    // log in as case worker
    await signInPage.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.generateGrantPreview);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    await signInPage.signOut();

    await basePage.logInfo(scenarioName, 'Check progress tab for Generate grant preview', caseRef);
    // log back in as solicitor
    await signInPage.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);
    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 7,
      numInProgress: 1,
      numNotStarted: 0,
      signOut: true});

    nextStepName = 'Find matches (Issue grant)';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    // log in as case worker
    await signInPage.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.findMatchesIssueGrant);
    await cwEventActionsPage.selectCaseMatches(caseRef, nextStepName);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    await signInPage.signOut();

    await basePage.logInfo(scenarioName, 'Check progress tab for Case Matching (Issue grant)');
    // log back in as solicitor
    await signInPage.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);
    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 8,
      numInProgress: 1,
      numNotStarted: 0,
      checkSubmittedDate: true,
      signOut: true});

    nextStepName = 'Issue grant';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    // log in as case worker
    await signInPage.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepConfig.issueGrant);
    await cwEventActionsPage.issueGrant(caseRef);
    await cwEventActionsPage.enterEventSummary(caseRef, nextStepName);
    await signInPage.signOut();

    await basePage.logInfo(scenarioName, 'Check progress tab for Issue grant');
    // log back in as solicitor
    await signInPage.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);

    await basePage.seeTabDetailsBilingual(caseRef, documentUploadSolTabConfigBilingual, caseProgressConfig);
    // await I.clickTab('Case Progress');

    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 9,
      numInProgress: 0,
      numNotStarted: 0,
      checkSubmittedDate: true,
      signOut: true});

    await basePage.logInfo(scenarioName, 'Scenario complete', caseRef);

  });
});
