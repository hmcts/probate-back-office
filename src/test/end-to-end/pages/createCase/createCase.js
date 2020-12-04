'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig');

module.exports = async function (jurisdiction, caseType, event) {

    const I = this;
    await I.waitForText(createCaseConfig.waitForText, testConfig.TestTimeToWaitForText || 60);

    await I.waitForEnabled({css: '#cc-jurisdiction'}, testConfig.TestTimeToWaitForText || 60);
    await I.selectOption('#cc-jurisdiction', jurisdiction);
    await I.waitForEnabled({css: '#cc-case-type'}, testConfig.TestTimeToWaitForText || 60);
    await I.selectOption('#cc-case-type', caseType);
    await I.waitForEnabled({css: '#cc-event'}, testConfig.TestTimeToWaitForText || 60);
    await I.selectOption('#cc-event', event);

    await I.waitForEnabled(createCaseConfig.startButton, testConfig.TestTimeToWaitForText || 60);
    await I.waitForNavigationToComplete(createCaseConfig.startButton);
};
