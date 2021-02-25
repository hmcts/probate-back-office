'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsPaymentMethods');
    await I.runAccessibilityTest();
    await I.selectOption('#solsPaymentMethods', completeApplicationConfig.page4_paymentType);
    await I.fillField('#solsFeeAccountNumber', completeApplicationConfig.page4_payAccountNum);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
