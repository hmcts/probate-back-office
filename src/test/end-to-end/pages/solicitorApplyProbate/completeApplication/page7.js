'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page7_waitForText);
    await I.runAccessibilityTest();
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayMedium);
    }
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
