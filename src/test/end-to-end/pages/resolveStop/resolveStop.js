'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function () {

    const I = this;

    await I.waitForEnabled({xpath: '//select[@id="resolveStopState"]'}, testConfig.WaitForTextTimeout || 60);
    await I.selectOption('//select[@id="resolveStopState"]', "Case selected for QA");
    await I.waitForEnabled(commonConfig.submitButton, testConfig.WaitForTextTimeout || 60);
    await I.wait(testConfig.CaseworkerGoButtonClickDelay);
    await I.waitForNavigationToComplete(commonConfig.submitButton);
};
