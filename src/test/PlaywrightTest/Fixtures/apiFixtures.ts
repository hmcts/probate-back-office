import { test as base } from "@playwright/test";
import { apiService } from "../APIServices/apiService.ts";

type ApiTestFixture = {
  callback: apiService
};

export const test = base.extend<ApiTestFixture>({
  callback: async ({request}, use) => {
    await use(new apiService(request));
  },
});
