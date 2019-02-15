'use strict';

const testConfig = require('src/test/config.js');
const withdrawalConfig = require('./withdrawalConfig.json');

module.exports = function () {

    const I = this;
    I.waitForText(withdrawalConfig.waitForText, testConfig.TestTimeToWaitForText);
    //  I.amOnPage(createCaseConfig.pageUrl)

    I.selectOption('#withdrawalReason', withdrawalConfig.list1_text);

    I.waitForNavigationToComplete(withdrawalConfig.continueButton);
};
