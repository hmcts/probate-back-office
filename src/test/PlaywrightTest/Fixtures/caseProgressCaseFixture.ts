import { test as base, expect } from "./apiFixtures.js";

type CaseProgressCaseFixtures = {
  freshCaseId: string;
};

export const test = base.extend<CaseProgressCaseFixtures>({
  freshCaseId: async ({}, use) => {
    await use("1784025144353635");
  },
});

export { expect };
