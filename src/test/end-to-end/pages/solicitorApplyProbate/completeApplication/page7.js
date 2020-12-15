'use strict';

const testConfig = require('src/test/config.js');
const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page7_waitForText, testConfig.TestTimeToWaitForText);

    await I.see(completeApplicationConfig.page7_applicationFee);
    await I.see(completeApplicationConfig.page7_additionalCopiesFee);
    await I.see(completeApplicationConfig.page7_feeForCertifiedCopies);
    await I.see(completeApplicationConfig.page7_totalFeeAmount);

    await I.waitForNavigationToComplete(commonConfig.goButton);
};
