import { test } from "../../Fixtures/fixtures.ts";

import { testConfig } from "../../Configs/config.ts";
import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import caseProgressConfig from "../../Pages/caseProgressStandard/caseProgressConfig.json" with { type: "json" };
import deceasedDetailsConfig from "../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };

test.describe("01 BO Case Progress E2E - application stopped: complete", () => {
  test("01 BO Case Progress E2E - application stopped: complete", async ({
      basePage,
      signInPage,
      createCasePage,
      solCreateCasePage,
      caseProgressPage
    }) => {
    const scenarioName ='Case Progress - application stopped path';

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'Login as Solicitor');

    await signInPage.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    // await I.waitForNavigationToComplete(commonConfig.continueButton, testConfig.CreateCaseContinueDelay);

    /* eslint-disable no-console */
    // @ts-ignore
    await basePage.logInfo(scenarioName, 'Initial application entry');
    await solCreateCasePage.applyForProbatePage1();
    await solCreateCasePage.applyForProbatePage2();
    await solCreateCasePage.cyaPage();
    await caseProgressPage.caseProgressCheckCaseProgressTab({
      numCompleted: 1,
      numInProgress: 0,
      numNotStarted: 1,
      linkText: 'Add deceased details',
      linkUrl: '/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1',
      goToNextStep: true});

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'Deceased details');
    // @ts-ignore
    await solCreateCasePage.deceasedDetailsPage1();
    // @ts-ignore
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

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'Add application details');
    await solCreateCasePage.grantOfProbatePage1();
    await solCreateCasePage.grantOfProbatePage2NoExecutors();
    await solCreateCasePage.grantOfProbatePage3();
    await solCreateCasePage.grantOfProbatePage4ExecNotApplying();
    await solCreateCasePage.grantOfProbatePage5();
    await solCreateCasePage.grantOfProbatePage6();
    await solCreateCasePage.cyaPage();

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'App stopped details');
    await caseProgressPage.caseProgressAppStoppedDetails();

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'App stopped tab check');
    await caseProgressPage.caseProgressAppStoppedTabCheck();
    await signInPage.signOut();

    // @ts-ignore
    await basePage.logInfo(scenarioName, '01 BO Case Progress E2E - application stopped: complete');
  });
});
