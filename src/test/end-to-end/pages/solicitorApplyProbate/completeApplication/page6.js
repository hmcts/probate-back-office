'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsPaymentMethods');
    await I.runAccessibilityTest();
    await I.selectOption('#solsPaymentMethods', completeApplicationConfig.page6_paymentType);
    await I.selectOption('#solsPBANumber', completeApplicationConfig.page6_pBAANumber);
    await I.fillField('#solsPBAPaymentReference', completeApplicationConfig.page6_paymentReference);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
