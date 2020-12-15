'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsPaymentMethods');

    await I.selectOption('#solsPaymentMethods', completeApplicationConfig.page5_paymentType);
    await I.fillField('#solsFeeAccountNumber', completeApplicationConfig.page5_payAccountNum);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
