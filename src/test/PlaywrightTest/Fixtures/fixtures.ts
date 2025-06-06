import { test as baseTest } from "@playwright/test";
import { PageFixtures, pageFixtures } from "../Fixtures/pageFixtures";

export type CustomFixtures = PageFixtures;

export const test = baseTest.extend<CustomFixtures>({
  ...pageFixtures,
});
