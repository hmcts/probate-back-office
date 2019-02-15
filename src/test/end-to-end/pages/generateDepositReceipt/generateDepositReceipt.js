'use strict';

const testConfig = require('src/test/config.js');
const generateDepositReceiptConfig = require('../eventSummary/generateDepositReceiptSummaryConfig.json');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(generateDepositReceiptConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.fillField('#field-trigger-summary', generateDepositReceiptConfig.summary);
    I.fillField('#field-trigger-description', generateDepositReceiptConfig.comment);

    I.waitForNavigationToComplete(generateDepositReceiptConfig.continueButton);

};
