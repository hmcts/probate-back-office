'use strict';

const testConfig = require('src/test/config.js');
const noticeOfChangeConfig = require('../noticeOfChange/noticeOfChangeConfig.json');

module.exports = async function () {

    const I = this;
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForText(noticeOfChangeConfig.nocWaitForText, testConfig.WaitForTextTimeout);
    await I.rejectCookies();

    const locator = noticeOfChangeConfig.xuiNocLocator;
    await I.waitForEnabled({css: locator});
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete(locator);
};
