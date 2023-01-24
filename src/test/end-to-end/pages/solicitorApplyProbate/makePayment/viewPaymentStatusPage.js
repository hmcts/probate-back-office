'use strict';

const testConfig = require('src/test/config.js');
const makePaymentConfig = require('./makePaymentConfig');

module.exports = async function (caseRef) {
    const I = this;
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.waitForText(makePaymentConfig.paymentStatusConfirmText, testConfig.WaitForTextTimeout);
    await I.see(makePaymentConfig.serviceRequestLink);
    await I.click(makePaymentConfig.serviceRequestLink);
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.waitForText(makePaymentConfig.paymentStatus, testConfig.WaitForTextTimeout);
    await I.dontSee(makePaymentConfig.payNowLinkText);
};
