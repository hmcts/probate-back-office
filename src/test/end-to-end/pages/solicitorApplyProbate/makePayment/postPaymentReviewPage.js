'use strict';

const testConfig = require('src/test/config.js');
const makePaymentConfig = require('./makePaymentConfig');
const postPaymentReviewTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/postPaymentReviewTabConfig');

module.exports = async function (caseRef) {
    const I = this;
    const tabXPath = `//div[contains(text(),"${makePaymentConfig.paymentTab}")]`;
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.waitForText(makePaymentConfig.reviewLinkText, testConfig.WaitForTextTimeout);
    await I.click(makePaymentConfig.reviewLinkText);
    await I.waitForElement(tabXPath, postPaymentReviewTabConfig.testTimeToWaitForTab || 60);
    await I.runAccessibilityTest();
    for (let i = 0; i < postPaymentReviewTabConfig.fields.length; i++) {
        if (postPaymentReviewTabConfig.fields[i] && postPaymentReviewTabConfig.fields[i] !== '') {
            await I.see(postPaymentReviewTabConfig.fields[i]); // eslint-disable-line no-await-in-loop
        }
    }

    await I.waitForElement('.govuk-back-link', testConfig.WaitForTextTimeout);
    await I.click(makePaymentConfig.backToPaymentLinkText);
};
