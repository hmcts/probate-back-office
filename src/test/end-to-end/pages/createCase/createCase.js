'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig');

module.exports = async function (jurisdiction, caseType, event) {

    const I = this;
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForText(createCaseConfig.waitForText, testConfig.WaitForTextTimeout || 60);
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForEnabled({css: '#cc-jurisdiction'}, testConfig.WaitForTextTimeout || 60);
    await I.waitForElement({css: '#cc-jurisdiction option[value=PROBATE]'}, testConfig.WaitForTextTimeout || 60);
    await I.selectOption('#cc-jurisdiction', jurisdiction);
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForEnabled({css: '#cc-case-type'}, testConfig.WaitForTextTimeout || 60);
    await I.retry(5).selectOption('#cc-case-type', caseType);
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForEnabled({css: '#cc-event'}, testConfig.WaitForTextTimeout || 60);
    await I.retry(5).selectOption('#cc-event', event);
    await I.wait(testConfig.CreateCaseDelay);

    await I.waitForEnabled(createCaseConfig.startButton, testConfig.WaitForTextTimeout || 60);
    await I.waitForNavigationToComplete(createCaseConfig.startButton);
    await I.wait(testConfig.CreateCaseDelay);
};
