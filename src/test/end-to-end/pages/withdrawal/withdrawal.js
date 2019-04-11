'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, withdrawalConfig) {

    const I = this;

    I.waitForText(withdrawalConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.selectOption('#withdrawalReason', withdrawalConfig.list1_text);

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
