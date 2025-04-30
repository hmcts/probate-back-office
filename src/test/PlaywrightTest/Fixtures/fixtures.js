const {mergeTests, mergeExpects} = require('@playwright/test');
const {test: pages} = require('./pageFixtures');
const {test: a11yTest, expect: a11yExpect} = require('./axeFixture');

export const test = mergeTests(pages, a11yTest);
export const expect = mergeExpects(a11yExpect);
