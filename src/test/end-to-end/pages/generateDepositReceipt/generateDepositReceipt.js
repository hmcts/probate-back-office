'use strict';

const testConfig = require('src/test/config.js');
const generateDepositReceiptConfig = require('../eventSummary/willLodgement/generateDepositReceiptSummaryConfig.json');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(generateDepositReceiptConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.fillField('#field-trigger-summary', generateDepositReceiptConfig.summary);
    I.fillField('#field-trigger-description', generateDepositReceiptConfig.comment);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
