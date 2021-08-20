'use strict';

const testConfig = require('src/test/config.js');
const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page3_waitForText, testConfig.WaitForTextTimeout);

    await I.runAccessibilityTest();

    await I.see(completeApplicationConfig.page3_app_ref);
    await I.see(completeApplicationConfig.page3_application_fee);
    await I.see(completeApplicationConfig.page3_pay_method);
    await I.see(completeApplicationConfig.page3_pBA_Account);
    await I.see(completeApplicationConfig.page3_customer_Reference);

    await I.waitForNavigationToComplete(commonConfig.submitButton);
};
