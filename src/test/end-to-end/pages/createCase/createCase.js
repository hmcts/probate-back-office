'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig');

module.exports = function (jurisdiction, caseType, event) {

    const I = this;
    I.waitForText(createCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.wait(20);
    I.selectOption('#cc-jurisdiction', jurisdiction);
    I.selectOption('#cc-case-type', caseType);
    I.selectOption('#cc-event', event);

    I.waitForNavigationToComplete(createCaseConfig.startButton);
};
