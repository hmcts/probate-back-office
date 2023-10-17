'use strict';

const testConfig = require('src/test/config.js');
const noticeOfChangeConfig = require('../noticeOfChange/noticeOfChangeConfig.json');

module.exports = async function (caseRef) {

    const I = this;
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForText(noticeOfChangeConfig.page1WaitForText, testConfig.WaitForTextTimeout);

    const locator = noticeOfChangeConfig.caseRefLocator;
    await I.waitForEnabled({css: locator});
    await I.fillField({css: locator}, caseRef);
    await I.wait(testConfig.CreateCaseDelay);
    await I.click(noticeOfChangeConfig.continueButtonLocator);
};
