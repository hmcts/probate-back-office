'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsPaymentMethods');

    await I.selectOption('#solsPaymentMethods', completeApplicationConfig.page1_payment_type);
    await I.fillField('#solsFeeAccountNumber', completeApplicationConfig.page1_pay_account_num);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
