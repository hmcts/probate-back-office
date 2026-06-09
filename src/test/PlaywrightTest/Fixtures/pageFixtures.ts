import { SignInPage } from "../Pages/IDAM/signIn.ts";
import { CwEventActionsPage } from "../Pages/newCase/cwEventActions.ts";
import { CreateCasePage } from "../Pages/newCase/newCase.ts";
import { SolCreateCasePage } from "../Pages/newCase/solNewCase.ts";
import { BasePage } from "../Pages/utility/basePage.ts";
import { CaseProgressPage } from "../Pages/caseProgressStandard/caseProgressCheck.ts";

export interface PageFixtures {
  basePage: BasePage;
  signInPage: SignInPage;
  createCasePage: CreateCasePage;
  cwEventActionsPage: CwEventActionsPage;
  solCreateCasePage: SolCreateCasePage;
  caseProgressPage: CaseProgressPage;
}

export const pageFixtures = {
  basePage: async ({ page }, use) => {
    await use(new BasePage(page));
  },

  signInPage: async ({ page }, use) => {
    await use(new SignInPage(page));
  },

  createCasePage: async ({ page }, use) => {
    await use(new CreateCasePage(page));
  },

  cwEventActionsPage: async ({ page }, use) => {
    await use(new CwEventActionsPage(page));
  },

  solCreateCasePage: async ({ page }, use) => {
    await use(new SolCreateCasePage(page));
  },

  caseProgressPage: async ({ page }, use) => {
    await use(new CaseProgressPage(page));
  },
};
