import { test as baseTest } from "@playwright/test";
import { PageFixtures, pageFixtures } from "../Fixtures/pageFixtures.ts";

export type CustomFixtures = PageFixtures;

export const test = baseTest.extend<CustomFixtures>({
  ...pageFixtures,
});
