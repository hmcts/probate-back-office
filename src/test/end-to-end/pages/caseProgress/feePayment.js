'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// grant of probate details part 10 - fee payment
module.exports = async function (caseProgressConfig) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('#solsPaymentMethods');
    await I.selectOption('select', caseProgressConfig.FeeAccount);
    await I.fillField('#solsFeeAccountNumber', caseProgressConfig.FeeAccountNumber);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
