'use strict';

const testConfig = require('src/test/config.js');
const makePaymentConfig = require('./makePaymentConfig');

module.exports = async function (caseRef) {
    const I = this;
    const delay = testConfig.CaseDetailsDelayDefault;
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.waitForText(makePaymentConfig.paymentStatusConfirmText, testConfig.WaitForTextTimeout);
    await I.see(makePaymentConfig.serviceRequestLink);
    await I.click(makePaymentConfig.serviceRequestLink);
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.waitForText(makePaymentConfig.paymentStatus, testConfig.WaitForTextTimeout);
    await I.dontSee(makePaymentConfig.payNowLinkText);

    const tabXPath = `//div[contains(text(),"${makePaymentConfig.mainTab}")]`;
    // Tabs are hidden when there are more tabs
    await I.waitForElement(tabXPath, 60);

    await I.waitForText(caseRef, testConfig.WaitForTextTimeout || 60);

    await I.clickTab(makePaymentConfig.mainTab);
    await I.wait(delay);
};
