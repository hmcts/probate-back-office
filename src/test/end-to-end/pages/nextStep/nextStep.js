'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function (nextStep) {

    const I = this;

    await I.waitForEnabled({css: '#next-step'}, testConfig.WaitForTextTimeout || 60);
    await I.selectOption('#next-step', nextStep);
    await I.waitForEnabled(commonConfig.submitButton, testConfig.WaitForTextTimeout || 60);
    await I.wait(testConfig.CaseworkerGoButtonClickDelay);
    await I.waitForNavigationToComplete(commonConfig.submitButton);
};
