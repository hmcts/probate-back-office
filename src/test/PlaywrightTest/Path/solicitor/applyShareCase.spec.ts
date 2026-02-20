import { test } from "../../Fixtures/fixtures.ts";

import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };

test.describe("Solicitor - Share A Case", () => {
  test("Solicitor - Share A Case", async ({
                                                    page,
                                                    basePage,
                                                    signInPage,
                                                    createCasePage,
                                                    solCreateCasePage
                                                  }) => {
    const scenarioName = 'Solicitor - Share A Case';
    const isSolicitorNamedExecutor = true;
    const isSolicitorApplyingExecutor = true;
    await signInPage.authenticateUserShareCase(true);
    const nextStepName = 'Deceased details';
    await basePage.logInfo(scenarioName, nextStepName);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(
      createCaseConfig.list2_text_gor,
      createCaseConfig.list3_text_solGor
    );
    await solCreateCasePage.applyForProbatePage1();
    await solCreateCasePage.applyForProbatePage2(
      isSolicitorNamedExecutor,
      isSolicitorApplyingExecutor
    );
    await solCreateCasePage.cyaPage();
    const sacCaseRef = await page.locator('//div[@class="column-one-half"]//ccd-case-header').textContent();
    const caseIdShareCase = sacCaseRef.replace(/#/g, '');
    const sacCaseRefNumber = sacCaseRef.replace(/\D/g, '');
    await basePage.logInfo(scenarioName, 'Select case for Share a Case: ', sacCaseRefNumber);
    await solCreateCasePage.shareCaseSelection(sacCaseRefNumber);
    await signInPage.signOut();
    // await I.logInfo(scenarioName, 'Login as PP user 2');
    await signInPage.authenticateUserShareCase(false);
    await basePage.logInfo(scenarioName, 'Verify Share a Case: ', sacCaseRefNumber);
    await solCreateCasePage.verifyShareCase(sacCaseRefNumber);
    await signInPage.signOut();
    // await I.logInfo(scenarioName, 'Login as PP user 1');
    // await I.logInfo(scenarioName, 'Verify Case ' + caseRef + ' is not shared with  PP user 1');
    await signInPage.authenticateUserShareCase(true);
    await basePage.logInfo(scenarioName, 'Verify Share a Case been removed: ', sacCaseRefNumber);
    await solCreateCasePage.shareCaseVerifyUserRemove(sacCaseRefNumber);
    await signInPage.signOut();
    // await I.logInfo(scenarioName, 'Login as PP user 2');
    // await I.logInfo(scenarioName, 'Delete the case ' + caseRef + ' and sign out');
    await signInPage.authenticateUserShareCase(false);
    await basePage.logInfo(scenarioName, 'Delete Case: ', sacCaseRefNumber);
    await solCreateCasePage.shareCaseDelete(caseIdShareCase, sacCaseRefNumber);
    await signInPage.signOut();
  });
});
