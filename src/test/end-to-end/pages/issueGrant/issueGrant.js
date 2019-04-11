'use strict';

const testConfig = require('src/test/config.js');
const issueGrantConfig = require('./issueGrantConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(issueGrantConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.click(`#boSendToBulkPrint-${issueGrantConfig.list1_text}`);
    I.click(`#boEmailGrantIssuedNotification-${issueGrantConfig.list2_text}`);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
