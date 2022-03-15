'use strict';

const testConfig = require('src/test/config.js');
const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// doc upload page
module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page3_waitForText, testConfig.WaitForTextTimeout);
    await I.runAccessibilityTest();
    await I.waitForNavigationToComplete(commonConfig.submitButton, true);
};
