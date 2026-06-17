import {test} from "../../../Fixtures/fixtures.ts";

import createCaseConfig from "../../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import deceasedDetailsConfig from "../../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };

test.describe("Solicitor - Grant of Representation Case Creation", () => {
  test("Solicitor - Grant of Representation Case Creation @webkit", async ({
    basePage,
    signInPage,
    createCasePage,
    solCreateCasePage,
    cwEventActionsPage
  }, testInfo) => {
    test.setTimeout(300000);
    const scenarioName = 'Solicitor - Grant of Representation Case Creation';
    const caseTypeKey = (process.env.CASE_TYPE ?? 'probate').toLowerCase();
    const isProbate = !['admon', 'intestacy'].includes(caseTypeKey);

    await basePage.logInfo(scenarioName, 'Login as Solicitor');
    await signInPage.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Apply for probate';
    let endState = 'Application created (deceased details)';
    await basePage.logInfo(scenarioName, nextStepName);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    await solCreateCasePage.applyForProbatePage1(isProbate);
    await solCreateCasePage.applyForProbatePage2(isProbate, isProbate);
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    const caseRef = await basePage.getCaseRefFromUrl();

    console.log(`Grant of Representation case created with reference: ${caseRef}`);

    nextStepName = 'Deceased details';
    const endStateMap: Record<string, string> = { admon: 'Admon will grant created', intestacy: 'Intestacy grant created' };
    endState = endStateMap[caseTypeKey] ?? 'Grant of probate created';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    const willTypeMap: Record<string, string> = { admon: 'WillLeftAnnexed', intestacy: 'NoWill' };
    const willType = willTypeMap[caseTypeKey] ?? 'WillLeft';
    await solCreateCasePage.deceasedDetailsPage1();
    await solCreateCasePage.deceasedDetailsPage2(undefined, undefined, 'IHT207');
    await solCreateCasePage.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT207');
    await solCreateCasePage.deceasedDetailsPage3(willType); // Information about the will page
    if (isProbate) {
      await solCreateCasePage.deceasedDetailsPage4();
    }
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    if (caseTypeKey === 'admon') {
      nextStepName = 'Admon will details';
      endState = 'Application updated';
      await basePage.logInfo(scenarioName, nextStepName, caseRef);
      await cwEventActionsPage.chooseNextStep(nextStepName);
      await solCreateCasePage.admonWillDetailsPage1();
      await solCreateCasePage.admonWillDetailsPage2(true);
      await solCreateCasePage.admonWillDetailsPage3();
      await solCreateCasePage.admonWillDetailsPage4();
      await solCreateCasePage.admonWillDetailsPage5();
      await solCreateCasePage.cyaPage();
      await solCreateCasePage.seeEndState(endState);

    } else if (caseTypeKey === 'intestacy') {
      nextStepName = 'Intestacy details';
      endState = 'Application updated';
      await basePage.logInfo(scenarioName, nextStepName, caseRef);
      await cwEventActionsPage.chooseNextStep(nextStepName);
      await solCreateCasePage.intestacyDetailsPage1();
      await solCreateCasePage.intestacyDetailsPage2();
      await solCreateCasePage.intestacyDetailsPage3();
      await solCreateCasePage.intestacyDetailsPage4();
      await solCreateCasePage.cyaPage();
      await solCreateCasePage.seeEndState(endState);

    } else {
      nextStepName = 'Grant of probate details';
      endState = 'Application updated';
      await basePage.logInfo(scenarioName, nextStepName, caseRef);
      await cwEventActionsPage.chooseNextStep(nextStepName);
      await solCreateCasePage.grantOfProbatePage1();
      await solCreateCasePage.grantOfProbatePage2(true, isProbate, isProbate);
      await solCreateCasePage.grantOfProbatePage4(false);
      await solCreateCasePage.grantOfProbatePage5();
      await solCreateCasePage.grantOfProbatePage6();
      await solCreateCasePage.cyaPage();
      await solCreateCasePage.seeEndState(endState);
    }

    nextStepName = 'Complete application';
    endState = 'Awaiting documentation';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.completeApplicationPage1(willType);
    await solCreateCasePage.completeApplicationPage3();
    await solCreateCasePage.completeApplicationPage4();
    await solCreateCasePage.completeApplicationPage5();
    await solCreateCasePage.completeApplicationPage6();
    await solCreateCasePage.completeApplicationPage7();
    await solCreateCasePage.completeApplicationPage8();

    console.log(`Case ${caseRef} created successfully and reached Awaiting Documentation`);
  });
});
