import { test } from "../../../../Fixtures/fixtures.ts";

import { testConfig } from "../../../../Configs/config.ts";
import createCaseConfig from "../../../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import standingSearchCases from "../../../../Pages/globalSearchStandingSearchCases/standingSearchCasesConfig.json" with {type: "json"};

test.describe("Data Creation for Global Search Testing - Standing Search", () => {
    test("Data Creation for Global Search Testing  - Standing Search @chromium", async ({ basePage, signInPage, createCasePage }) => {
        const scenarioName = "Data Creation for Global Search Testing  - Standing Search";

        await basePage.logInfo(scenarioName, "Login as Caseworker");
        await signInPage.authenticateWithIdamIfAvailable(false);

        for (const caseConfig of standingSearchCases) {
            let nextStepName = "Create a standing search";
            await basePage.logInfo(scenarioName, nextStepName);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(
                createCaseConfig.list2_text_stan_search,
                createCaseConfig.list3_text_stan_search
            );
            await createCasePage.enterStandingSearchPage1("create");
            await createCasePage.enterStandingSearchPage2(
                "create",
                caseConfig
            );

            await createCasePage.enterStandingSearchPage3(
                "create",
                caseConfig
            );
            await createCasePage.enterStandingSearchPage4("create");
            await createCasePage.checkMyAnswers(nextStepName);
        }
    });
});
