'use strict';

const testConfig = require('src/test/config.cjs');

module.exports = async function (sacCaseRefNumber) {
    const I = this;
    await I.waitForText('Your cases', 20);
    await I.wait(testConfig.CreateCaseDelay);
    await I.click('//div[normalize-space()="Case reference"]');
    await I.wait(2);
    await I.dontSeeElement('//input[@id="select-' + sacCaseRefNumber + '"]');
    await I.click('//a[normalize-space()="Sign out"]');
    // await I.logInfo(scenarioName, 'PP1 User verified unshared caseRef: '+caseRef+'');
};
