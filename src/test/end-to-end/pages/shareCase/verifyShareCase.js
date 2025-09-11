'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (SacCaseRefNumber) {
    const I = this;
    await I.waitForText('Your cases', 20);
    await I.wait(4);
    await I.click('//div[normalize-space()="Case reference"]');
    await I.wait(2);
    await I.click('//input[@id="select-' + SacCaseRefNumber + '"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete('#btn-share-button', 2);
    await I.click('#accordion-with-summary-sections > div > button > span.govuk-accordion__show-all-text');
    await I.wait(1);
    await I.click('Remove', '//tr[contains(.,"' + testConfig.TestEnvProfUser + '")]');
    await I.waitForText('TO BE REMOVED', 5);
    await I.waitForNavigationToComplete('button[title="Continue"]');
    await I.waitForNavigationToComplete('button[title="Confirm"]');
    await I.waitForText('Your cases have been updated', 5);
    await I.click('//a[normalize-space()="Sign out"]');

};
