'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = async function () {

    const I = this;

    await I.waitForText(newCaseConfig.waitForText, testConfig.TestTimeToWaitForText);
    I.logPageHtml(true);

    try {
        await I.waitForNavigationToComplete(testConfig.TestForXUI ? newCaseConfig.xuiCreateCaseLocator : newCaseConfig.ccduilCreateCaselocator, 120);
    } catch (e) {
        I.logPageHtml(false, e);
        throw e;
    }
    I.logPageHtml(false);
};
