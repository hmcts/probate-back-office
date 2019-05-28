'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = function () {

    const I = this;

    I.waitForText(newCaseConfig.waitForText, testConfig.TestTimeToWaitForText);
    I.waitForNavigationToComplete(newCaseConfig.locator);
};
