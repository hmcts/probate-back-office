'use strict';

const testConfig = require('src/test/config.js');
const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page3_waitForText, testConfig.TestTimeToWaitForText);

    await I.see(completeApplicationConfig.page3_app_ref);
    await I.see(completeApplicationConfig.page3_application_fee);
    await I.see(completeApplicationConfig.page3_pay_method);
    await I.see(completeApplicationConfig.page3_pay_ref);

    await I.waitForNavigationToComplete(commonConfig.goButton);
};
