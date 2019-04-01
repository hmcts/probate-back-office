'use strict';

const testConfig = require('src/test/config.js');
const selectCaseConfig = require('./selectCaseConfig.json');

module.exports = function (jurisdiction, caseType, event) {

    const I = this;
    I.waitForText(selectCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.wait(20);

    I.waitForNavigationToComplete(selectCaseConfig.caseLink);
};
