'use strict';

const testConfig = require('src/test/config.js');
const printCaseConfig = require('./printCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(printCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.selectOption('#casePrinted', printCaseConfig.list1_text);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
