import { test } from "../../Fixtures/fixtures.ts";
import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import caveatCases from "../../Pages/globalSearchCaveatCases/caveatCasesConfig.json" with {type: "json"};

test.describe("Data Creation for Global Search Testing - Grant of Representation", () => {
    test("Data Creation for Global Search Testing - Caveat @chromium", async ({ basePage, signInPage, createCasePage }) => {
        const scenarioName = "Data Creation for Global Search Testing - Grant of Representation";

        await basePage.logInfo(scenarioName, "Login as Caseworker", undefined);
        await signInPage.authenticateWithIdamIfAvailable(false);

        for(const caseConfig of caveatCases) {
            let nextStepName = "Raise a caveat";
            await basePage.logInfo(scenarioName, nextStepName, undefined);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(
                createCaseConfig.list2_text_caveat,
                createCaseConfig.list3_text_caveat
            );
            await createCasePage.enterCaveatPage1("create");
            await createCasePage.enterCaveatPage2(
                "create",
                "",
                caseConfig
            );
            await createCasePage.enterCaveatPage3(
                "create",
                caseConfig
            );
            await createCasePage.enterCaveatPage4("create");
            await createCasePage.checkMyAnswers(nextStepName);
        }
    });
});
