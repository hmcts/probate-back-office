'use strict';

const testConfig = require('src/test/config.js');
const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page6_waitForText, testConfig.TestTimeToWaitForText);
    await I.runAccessibilityTest();
    await I.see(completeApplicationConfig.page6_applicationFee);
    await I.see(completeApplicationConfig.page6_additionalCopiesFee);
    await I.see(completeApplicationConfig.page6_feeForCertifiedCopies);
    await I.see(completeApplicationConfig.page6_totalFeeAmount);

    await I.waitForNavigationToComplete(commonConfig.goButton);
};
