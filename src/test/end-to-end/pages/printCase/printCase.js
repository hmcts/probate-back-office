'use strict';

const testConfig = require('src/test/config.js');
const printCaseConfig = require('./printCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {

    const I = this;
    await I.waitForText(printCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);

    await I.selectOption('#casePrinted', printCaseConfig.list1_text);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
