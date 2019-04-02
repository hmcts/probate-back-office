'use strict';

const testConfig = require('src/test/config.js');
const markAsReadyConfig = require('./markAsReadyConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(markAsReadyConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.click(`#boEmailDocsReceivedNotification-${markAsReadyConfig.list1_text}`);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
