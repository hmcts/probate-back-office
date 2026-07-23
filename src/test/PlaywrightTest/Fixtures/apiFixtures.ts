import { test as base, expect } from "@playwright/test";
import { apiService } from "../APIServices/apiService.ts";

type ApiTestFixture = {
  caseApiService: apiService;
};

export const test = base.extend<ApiTestFixture>({
  caseApiService: async ({ request }, use) => {
    await use(new apiService(request));
  },
});

export { expect };
