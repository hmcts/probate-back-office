import {test} from "../../Fixtures/fixtures.ts";

import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };

import applyProbateConfig from "../../Pages/solicitorApplyProbate/applyProbate/applyProbateConfig.json" with { type: "json" };
import deceasedDetailsConfig from "../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };

import applicantDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig.json" with { type: "json" };
import deceasedTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/deceasedTabConfig.json" with { type: "json" };
import caseDetailsTabDeceasedDtlsConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfig.json" with { type: "json" };
import caseDetailsTabUpdatesConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig.json" with { type: "json" };
import historyTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/historyTabConfig.json" with { type: "json" };
import caseProgressConfig from "../../Pages/caseProgressStandard/caseProgressConfig.json" with { type: "json" };

test.describe("Solicitor - Apply Grant of probate Admon Will (Will left annexed) - Stopped", () => {
  test("Solicitor - Apply Grant of probate Admon Will (Will left annexed) - Stopped", async ({
    basePage,
    signInPage,
    createCasePage,
    solCreateCasePage,
    cwEventActionsPage
  }, testInfo) => {
    const scenarioName = 'Solicitor - Apply Grant of probate Admon Will (Will left annexed) - Stopped';
    const isSolicitorExecutor = true;
    const isSolicitorMainApplicant = true;
    const willType = 'WillLeftAnnexed';
    const updateAddressManually = true;

    // @ts-ignore
    await basePage.logInfo(scenarioName, 'Login as Solicitor');
    await signInPage.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Apply for probate';
    let endState = 'Application created (deceased details)';
    // @ts-ignore
    await basePage.logInfo(scenarioName, 'New case');
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    await solCreateCasePage.applyForProbatePage1();
    await solCreateCasePage.applyForProbatePage2(isSolicitorExecutor, isSolicitorMainApplicant);
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    const caseRef = await basePage.getCaseRefFromUrl();

    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, applicantDetailsTabConfig, applyProbateConfig);

    nextStepName = 'Deceased details';
    endState = 'Admon will grant created';

    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    // @ts-ignore
    await solCreateCasePage.deceasedDetailsPage1();
    // @ts-ignore
    await solCreateCasePage.deceasedDetailsPage2();
    await solCreateCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
    await solCreateCasePage.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT400');
    await solCreateCasePage.deceasedDetailsPage3(willType);
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, deceasedTabConfig, deceasedDetailsConfig);
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig);
    await basePage.seeUpdatesOnCase(testInfo, caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig);

    nextStepName = 'Admon will details';
    endState = 'Stopped';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.admonWillDetailsPage1();
    await solCreateCasePage.admonWillDetailsPage2(updateAddressManually);
    await solCreateCasePage.admonWillDetailsPage3();
    await solCreateCasePage.admonWillDetailsPage4();
    await solCreateCasePage.admonWillDetailsPage5();

    await solCreateCasePage.cyaPage();

    await solCreateCasePage.admonWillDetailsPage6();
    await solCreateCasePage.seeEndState(endState);

  });
});
