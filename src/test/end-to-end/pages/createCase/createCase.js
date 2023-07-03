'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig');
async function waitForElementVisible(selector, timeout = 15000) {
    const start = Date.now();

    while (Date.now() - start < timeout) {
        const el = document.querySelector(selector);
        if (el) {
            return el;
        }
        await new Promise(resolve => setTimeout(resolve, 1000));
    }

    return null;
}
module.exports = async function (caseType, event) {

    const I = this;
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForText(createCaseConfig.waitForText, testConfig.WaitForTextTimeout || 60);
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForEnabled({css: '#cc-jurisdiction'}, testConfig.WaitForTextTimeout || 60);
    //await I.waitForSelector({css: '#cc-jurisdiction option[value=PROBATE]'});
    await I.waitForElement({css: '#cc-jurisdiction option[value=PROBATE]'},400);
    await I.selectOption('#cc-jurisdiction', 'PROBATE');
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
