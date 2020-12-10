'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = function (xui = false) {

    const I = this;

    I.waitForText(newCaseConfig.waitForText, testConfig.TestTimeToWaitForText);
    if (xui) {
        I.waitForNavigationToComplete(newCaseConfig.xuiCreateCaseLocator);
    } else {
        I.waitForNavigationToComplete(newCaseConfig.ccduilCreateCaselocator);
    }
};
