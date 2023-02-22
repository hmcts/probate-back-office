'use strict';

const testConfig = require('src/test/config.js');
const makePaymentConfig = require('./makePaymentConfig');

module.exports = async function (caseRef,serviceRequestReviewTabConfig) {
    const I = this;
    const tabXPath = `//div[contains(text(),"${makePaymentConfig.paymentTab}")]`;
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.waitForElement(tabXPath, serviceRequestReviewTabConfig.testTimeToWaitForTab || 60);
    await I.runAccessibilityTest();
    for (let i = 0; i < serviceRequestReviewTabConfig.fields.length; i++) {
        if (serviceRequestReviewTabConfig.fields[i] && serviceRequestReviewTabConfig.fields[i] !== '') {
            await I.see(serviceRequestReviewTabConfig.fields[i]);
        }
    }

    await I.waitForElement('#bckLnksize', testConfig.WaitForTextTimeout);
    await I.click(makePaymentConfig.backToPaymentLinkText);
};
