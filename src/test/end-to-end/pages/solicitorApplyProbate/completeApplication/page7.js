'use strict';

const testConfig = require('src/test/config.js');
const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page7_waitForText, testConfig.TestTimeToWaitForText);
    await I.runAccessibilityTest();
    await I.see(completeApplicationConfig.page7_applicationFee);
    // We are getting different fees when running locally (using CCD), not sure if launch darkly issue.
    // So retain this test just for when we are running in pipeline on EXUI
    if (testConfig.TestForXUI) {
        await I.see(completeApplicationConfig.page7_additionalCopiesFee);
        await I.see(completeApplicationConfig.page7_feeForCertifiedCopies);
        await I.see(completeApplicationConfig.page7_totalFeeAmount);    
    }

    await I.waitForNavigationToComplete(commonConfig.goButton);
};
