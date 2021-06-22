'use strict';

const testConfig = require('src/test/config.js');
const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page8_waitForText, testConfig.TestTimeToWaitForText);
    await I.runAccessibilityTest();
    await I.see(completeApplicationConfig.page8_applicationFee);
    await I.see(completeApplicationConfig.page8_additionalCopiesFee);
    await I.see(completeApplicationConfig.page8_feeForCertifiedCopies);
    await I.see(completeApplicationConfig.page8_totalFeeAmount);
    await I.see(completeApplicationConfig.page8_paymentMethod);
    await I.see(completeApplicationConfig.page8_pBAAccount);
    await I.see(completeApplicationConfig.page8_customerReference);

    await I.waitForNavigationToComplete(commonConfig.goButton);
};
