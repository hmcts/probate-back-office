'use strict';

const testConfig = require('src/test/config.js');
const noticeOfChangeConfig = require('../noticeOfChange/noticeOfChangeConfig.json');

module.exports = async function (caseRef) {

    const I = this;
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForText(noticeOfChangeConfig.confirmationPageWaitForText, testConfig.WaitForTextTimeout);

    await I.see(caseRef);
    await I.waitForEnabled({css: noticeOfChangeConfig.viewCaseLinkLocator});
    await I.click('#content > div > exui-noc-navigation > div > div > exui-noc-submit-success > div > div > div > div > p:nth-child(8) > a');
};
