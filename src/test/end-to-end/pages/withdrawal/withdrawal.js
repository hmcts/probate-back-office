'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, withdrawalConfig) {

    const I = this;

    await I.waitForText(withdrawalConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);

    await I.selectOption('#withdrawalReason', withdrawalConfig.list1_text);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
