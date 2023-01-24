'use strict';

const testConfig = require('src/test/config.js');
const makePaymentConfig = require('./makePaymentConfig');

module.exports = async function (caseRef) {
    const I = this;
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.waitForText(makePaymentConfig.payNowLinkText);
    await I.click(makePaymentConfig.payNowLinkText);
    await I.waitForText(makePaymentConfig.page2_waitForText, testConfig.WaitForTextTimeout);
    await I.runAccessibilityTest();
    await I.waitForElement('#pbaAccount');
    await I.checkOption('#pbaAccount');
    await I.waitForElement('#pbaAccountNumber');
    await I.selectOption('#pbaAccountNumber', makePaymentConfig.page2_pBAANumber);
    await I.fillField('#pbaAccountRef', makePaymentConfig.page2_paymentReference);
    await I.click('//label[normalize-space()=\'Enter a reference for your PBA account statements\']');
    await I.click('button[type="submit"]');
};
