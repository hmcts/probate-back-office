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
    const isSolicitorNamedExecutor = true;
    const isSolicitorApplyingExecutor = true;

    await basePage.logInfo(scenarioName, 'Login as Solicitor');
    await signInPage.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Apply for probate';
    let endState = 'Application created (deceased details)';
    await basePage.logInfo(scenarioName, nextStepName);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    await solCreateCasePage.applyForProbatePage1();
    await solCreateCasePage.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    const caseRef = await basePage.getCaseRefFromUrl();

    console.log(`Grant of Representation case created with reference: ${caseRef}`);

    nextStepName = 'Deceased details';
    endState = 'Grant of probate created';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.deceasedDetailsPage1();
    await solCreateCasePage.deceasedDetailsPage2(undefined, undefined, 'IHT207');
    await solCreateCasePage.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT207');
    await solCreateCasePage.deceasedDetailsPage3();
    await solCreateCasePage.deceasedDetailsPage4();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    nextStepName = 'Grant of probate details';
    endState = 'Application updated';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.grantOfProbatePage1();
    await solCreateCasePage.grantOfProbatePage2(true, isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await solCreateCasePage.grantOfProbatePage4(false);
    await solCreateCasePage.grantOfProbatePage5();
    await solCreateCasePage.grantOfProbatePage6();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    nextStepName = 'Complete application';
    endState = 'Awaiting documentation';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.completeApplicationPage1();
    await solCreateCasePage.completeApplicationPage3();
    await solCreateCasePage.completeApplicationPage4();
    await solCreateCasePage.completeApplicationPage5();
    await solCreateCasePage.completeApplicationPage6();
    await solCreateCasePage.completeApplicationPage7();
    await solCreateCasePage.completeApplicationPage8();

    console.log(`Case ${caseRef} created successfully and reached Awaiting Documentation`);
  });
});