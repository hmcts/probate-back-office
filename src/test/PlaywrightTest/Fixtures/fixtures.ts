import { PageFixtures, pageFixtures } from "../Fixtures/pageFixtures.ts";
import { HelperFixtures, helperFixtures } from "./helperFixtures.ts";

export type CustomFixtures = PageFixtures & HelperFixtures;

export const test = helperFixtures.extend<CustomFixtures>({
  ...pageFixtures,
});
