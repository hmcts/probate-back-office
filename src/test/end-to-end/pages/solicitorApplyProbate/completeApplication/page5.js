'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsPaymentMethods');
    await I.runAccessibilityTest();
    await I.selectOption('#solsPaymentMethods', completeApplicationConfig.page5_paymentType);
    await I.selectOption('#solsPBANumber', completeApplicationConfig.page5_pBAANumber);
    await I.fillField('#solsPBAPaymentReference', completeApplicationConfig.page5_paymentReference);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
