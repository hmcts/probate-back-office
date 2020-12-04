'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig');

module.exports = async function (jurisdiction, caseType, event) {

    const I = this;
    await I.waitForText(createCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.selectOption('#cc-jurisdiction', jurisdiction);
    await I.selectOption('#cc-case-type', caseType);
    await I.selectOption('#cc-event', event);

    await I.waitForNavigationToComplete(createCaseConfig.startButton);
};
