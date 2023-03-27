'use strict';

const testConfig = require('src/test/config.js');
const makePaymentConfig = require('./makePaymentConfig');

module.exports = async function (caseRef, serviceRequestTabConfig) {
    const I = this;
    const tabXPath = `//div[contains(text(),"${makePaymentConfig.paymentTab}")]`;
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.waitForElement(tabXPath, serviceRequestTabConfig.testTimeToWaitForTab || 60);
    await I.clickTab(makePaymentConfig.paymentTab);
    await I.runAccessibilityTest();
    for (let i = 0; i < serviceRequestTabConfig.fields.length; i++) {
        if (serviceRequestTabConfig.fields[i] && serviceRequestTabConfig.fields[i] !== '') {
            await I.see(serviceRequestTabConfig.fields[i]);
        }
    }

    await I.waitForText(makePaymentConfig.reviewLinkText, testConfig.WaitForTextTimeout);
    await I.click(makePaymentConfig.reviewLinkText);
};
