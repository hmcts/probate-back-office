'use strict';

const testConfig = require('src/test/config');
const reopenCaveatConfig = require('./reopenCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(reopenCaveatConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.fillField('#caveatReopenReason', reopenCaveatConfig.reopen_caveat_reason);

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
