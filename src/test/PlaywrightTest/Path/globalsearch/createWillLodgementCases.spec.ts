import { test } from "../../Fixtures/fixtures.ts";
import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import willLodgementCases from "../../Pages/globalSearchWillLodgementCases/willLodgementCasesConfig.json" with {type: "json"};


test.describe("Data Creation for Global Search Testing - Will Lodgement", () => {
    test("Data Creation for Global Search Testing - Will Lodgement", async ({ basePage, signInPage, createCasePage }) => {
        const scenarioName = "Data Creation for Global Search Testing - Will Lodgement";
        // 1. Sign in
        await basePage.logInfo(scenarioName, "Login as Caseworker", undefined);
        await signInPage.authenticateWithIdamIfAvailable(false);

        for(const caseConfig of willLodgementCases){
            let nextStepName = "Create a will lodgement";
            await basePage.logInfo(scenarioName, nextStepName, undefined);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(
                createCaseConfig.list2_text_will,
                createCaseConfig.list3_text_will
            );
            await createCasePage.enterWillLodgementPage1(
                "create",
                caseConfig 
            );
            await createCasePage.enterWillLodgementPage2(
                "create",
                caseConfig
            );
            await createCasePage.enterWillLodgementPage3(
                "create",
                caseConfig
            );
            await createCasePage.checkMyAnswers(nextStepName);
        }
    });
});
