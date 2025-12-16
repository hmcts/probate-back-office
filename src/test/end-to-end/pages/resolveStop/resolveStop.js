'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.cjs');

module.exports = async function (resolveStop) {

    const I = this;
    await I.waitForEnabled({css: '#resolveStopState'}, testConfig.WaitForTextTimeout || 60);
    await I.selectOption('#resolveStopState', resolveStop);
    await I.waitForEnabled(commonConfig.submitButton, testConfig.WaitForTextTimeout || 60);
    await I.wait(testConfig.CaseworkerGoButtonClickDelay);
    await I.waitForNavigationToComplete(commonConfig.submitButton);
};
