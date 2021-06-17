'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsPaymentMethods');
    await I.runAccessibilityTest();

    await I.selectOption('#solsPaymentMethods', completeApplicationConfig.page1_payment_type);
    await I.selectOption('#solsPBANumber', completeApplicationConfig.page1_pBAANumber);
    await I.fillField('#solsPBAPaymentReference', completeApplicationConfig.page1_paymentReference);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
