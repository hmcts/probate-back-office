'use strict';

const testConfig = require('src/test/config.js');
const noticeOfChangeConfig = require('../noticeOfChange/noticeOfChangeConfig.json');

module.exports = async function (deceasedSurname) {

    const I = this;
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForText(noticeOfChangeConfig.page2WaitForText, testConfig.WaitForTextTimeout);

    const locator = noticeOfChangeConfig.deceasedSurnameLocator;
    await I.waitForEnabled({css: locator});
    await I.fillField({css: locator}, deceasedSurname);
    await I.wait(testConfig.CreateCaseDelay);
    await I.click(noticeOfChangeConfig.continueButtonLocator);
};
