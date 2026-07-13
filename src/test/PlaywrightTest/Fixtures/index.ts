import { mergeTests } from '@playwright/test';
import { test as apiTest } from './apiFixtures.ts';
import { test as pageTest } from "./fixtures.ts";

export const test = mergeTests(apiTest, pageTest);
export { expect } from '@playwright/test';
