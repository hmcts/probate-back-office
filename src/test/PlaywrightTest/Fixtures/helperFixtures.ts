import { test as base } from "@playwright/test";
import { WorkAllocation } from "../helpers/workAllocation.ts";

export type HelperFixtures = {
  waEnabled: boolean;
};

export const helperFixtures = base.extend<{}, HelperFixtures>({
  waEnabled: [async ({}, use) => {
    const isEnabled = await WorkAllocation.isWaEnabled();
    await use(isEnabled);
    //scoped so that it only runs once per worker and caches it as config is static
  }, { scope: "worker" }],
});