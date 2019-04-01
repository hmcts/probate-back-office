'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, generateDepositReceiptSummaryConfig) {

    const I = this;
    I.waitForText(generateDepositReceiptSummaryConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.fillField('#field-trigger-summary', generateDepositReceiptSummaryConfig.summary);
    I.fillField('#field-trigger-description', generateDepositReceiptSummaryConfig.comment);

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
