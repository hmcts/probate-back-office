'use strict';

const testConfig = require('src/test/config');
const reopenCaveatConfig = require('./reopenCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {

    const I = this;
    await I.waitForText(reopenCaveatConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);

    await I.fillField('#caveatReopenReason', reopenCaveatConfig.reopen_caveat_reason);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
