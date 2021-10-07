'use strict';

const testConfig = require('src/test/config');
const withdrawCaveatConfig = require('./withdrawCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {

    const I = this;

    await I.waitForText(withdrawCaveatConfig.page1_waitForText, testConfig.WaitForTextTimeout);
    await I.waitForElement(`#caveatRaisedEmailNotificationRequested_${withdrawCaveatConfig.page1_optionNo}`);
    await I.click(`#caveatRaisedEmailNotificationRequested_${withdrawCaveatConfig.page1_optionNo}`);

    await I.waitForText(withdrawCaveatConfig.page1_send_bulk_print, testConfig.WaitForTextTimeout);
    await I.click(`#sendToBulkPrintRequested_${withdrawCaveatConfig.page1_optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
