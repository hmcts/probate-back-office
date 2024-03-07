'use strict';

const testConfig = require('src/test/config.js');
const makePaymentConfig = require('./makePaymentConfig');

module.exports = async function (caseRef, appType) {
    const I = this;
    const tabXPath = `//div[contains(text(),"${makePaymentConfig.eventHistoryTab}")]`;
    const caseProgressTabXPath = `//div[contains(text(),"${makePaymentConfig.caseProgressTab}")]`;
    const caseRefNoDashes = await I.replaceAll(caseRef, '-', '');
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.seeInSource(makePaymentConfig.paymentStatusConfirmText);
    await I.waitForText(makePaymentConfig.serviceRequestLink, testConfig.WaitForTextTimeout);
    await I.forceClick('View service requests');
    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);
    await I.waitForText(makePaymentConfig.paymentStatus, testConfig.WaitForTextTimeout);
    await I.dontSee(makePaymentConfig.payNowLinkText);
    await I.postPaymentReviewDetails(caseRef);
    //const caseRefNoDashes = caseRef.toString().replace(/-/g, '');
    // Tabs are hidden when there are more tabs
    for (let i = 0; i <= 6; i++) {
        await I.waitForElement(tabXPath, 60); // eslint-disable-line no-await-in-loop
        await I.waitForText(caseRef, testConfig.WaitForTextTimeout || 60); // eslint-disable-line no-await-in-loop
        await I.clickTab(makePaymentConfig.eventHistoryTab); // eslint-disable-line no-await-in-loop
        //const result = await I.checkForText(makePaymentConfig.statusText, 10); // eslint-disable-line no-await-in-loop
        const pageSource = await I.grabSource(); // eslint-disable-line no-await-in-loop
        const result = pageSource.includes(makePaymentConfig.statusText);
        if (result === true) {
            break;
        }
        await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/${caseRefNoDashes}`); // eslint-disable-line no-await-in-loop
    }
    if (appType !== 'Caveat') {
        await I.waitForElement(caseProgressTabXPath, 60);
        await I.waitForText(caseRef, testConfig.WaitForTextTimeout || 60);
        await I.clickTab(makePaymentConfig.caseProgressTab);
    }
};
