import { test } from "../../../../Fixtures/fixtures.ts";

import { testConfig } from "../../../../Configs/config.ts";
import createCaseConfig from "../../../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import caseProgressConfig from "../../../../Pages/caseProgressStandard/caseProgressConfig.json" with { type: "json" };
import grantOfProbateCases from "../../../../Pages/globalSearchGrantOfProbateCases/grantOfProbateCasesConfig.json" with {type: "json"};

test.describe("Data Creation for Global Search Testing - Grant of Representation", () => {
    test("Data Creation for Global Search Testing - Grant of Representation @chromium", async ({ basePage, signInPage, createCasePage }) => {
        const scenarioName = "Data Creation for Global Search Testing - Grant of Representation";

        await basePage.logInfo(scenarioName, "Login as Caseworker");
        await signInPage.authenticateWithIdamIfAvailable(false);

        for (const caseConfig of grantOfProbateCases) {
            let nextStepName = "PA1P/PA1A/Solicitors Manual";
            await basePage.logInfo(scenarioName, nextStepName);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(
                createCaseConfig.list2_text_gor,
                createCaseConfig.list3_text_gor_manual
            );
            await createCasePage.enterGrantOfProbateManualPage1(
                "create",
                caseConfig
            );
            await createCasePage.enterGrantOfProbateManualPage2("createIHT400");
            await createCasePage.enterIhtDetails(
                caseProgressConfig,
                caseProgressConfig.optionYes
            );
            await createCasePage.enterGrantOfProbateManualPage3(
                "create",
                caseConfig
            );
            await createCasePage.checkMyAnswers(nextStepName);
        }
    });
});
