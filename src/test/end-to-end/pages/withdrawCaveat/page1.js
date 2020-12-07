'use strict';

const testConfig = require('src/test/config');
const withdrawCaveatConfig = require('./withdrawCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function () {

    const I = this;

    I.waitForText(withdrawCaveatConfig.page1_waitForText, testConfig.TestTimeToWaitForText);
    I.click(`#caveatRaisedEmailNotificationRequested-${withdrawCaveatConfig.page1_optionNo}`);

    I.waitForText(withdrawCaveatConfig.page1_send_bulk_print, testConfig.TestTimeToWaitForText);
    I.click(`#sendToBulkPrintRequested-${withdrawCaveatConfig.page1_optionNo}`);

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
