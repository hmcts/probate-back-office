'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = async function () {

    const I = this;

    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForText(newCaseConfig.waitForText, testConfig.WaitForTextTimeout);
    await I.rejectCookies();

    const locator = newCaseConfig.xuiCreateCaseLocator;
    await I.waitForEnabled({css: locator});
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete(locator);
};
