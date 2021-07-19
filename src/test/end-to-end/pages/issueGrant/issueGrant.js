'use strict';

const testConfig = require('src/test/config.js');
const issueGrantConfig = require('./issueGrantConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {

    const I = this;
    await I.waitForText(issueGrantConfig.waitForText, testConfig.WaitForTextTimeout);

    await I.see(caseRef);

    await I.click(`#boSendToBulkPrint_${issueGrantConfig.list1_text}`);
    await I.click(`#boEmailGrantIssuedNotification_${issueGrantConfig.list2_text}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);

};
