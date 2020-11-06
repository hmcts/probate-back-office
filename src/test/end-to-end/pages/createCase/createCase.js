'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig');

module.exports = function (jurisdiction, caseType, event, waitTime) {

    const I = this;
    waitTime = waitTime === null || waitTime === undefined ? 20 : waitTime;
    I.waitForText(createCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.wait(waitTime);
    I.selectOption('#cc-jurisdiction', jurisdiction);
    I.selectOption('#cc-case-type', caseType);
    I.selectOption('#cc-event', event);

    I.waitForNavigationToComplete(createCaseConfig.startButton);
};
