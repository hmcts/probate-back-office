'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = async function () {

    const I = this;

    await I.saveScreenshot('debugNightly.png', true);
    await I.waitForText(newCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    try {
        // eslint-disable-next-line
        await I.waitForNavigationToComplete(testConfig.TestForXUI ? newCaseConfig.xuiCreateCaseLocator : newCaseConfig.ccduilCreateCaselocator, 120);
    } catch (e) {
        await I.saveScreenshot('debugNightlyInCatchBlock.png', true);
        throw e;
    }
    await I.saveScreenshot('debugNightlyAfterExecution.png', true);

};
