'use strict';

const testConfig = require('src/test/config.cjs');

module.exports = async function (sacCaseRefNumber) {

    const I = this;
    await I.wait(testConfig.CreateCaseDelay);
    await I.click('//a[normalize-space()="Case list"]');
    await I.waitForText('Your cases', 20);
    await I.selectOption('#wb-jurisdiction', 'Manage probate application');
    await I.selectOption('#wb-case-type', 'Grant of representation');
    await I.click('//button[normalize-space()="Apply"]');
    await I.wait(4);
    await I.click('//div[normalize-space()="Case reference"]');
    await I.wait(2);
    await I.waitForElement('//input[@id="select-' + sacCaseRefNumber + '"]');
    await I.click('//input[@id="select-' + sacCaseRefNumber + '"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete('#btn-share-button', testConfig.CreateCaseDelay);
    await I.wait(2);
    await I.fillField('input[role="combobox"]', 'T');
    await I.wait(3);
    await I.click('//span[contains(text(),"probate.practitioner.aat.test@gmail.com")]');
    await I.click('#btn-add-user');
    await I.click('#accordion-with-summary-sections > div > button > span.govuk-accordion__show-all-text');
    await I.see('probate.practitioner.aat.test@gmail.com');
    await I.see('TO BE ADDED');
    await I.waitForNavigationToComplete('button[title="Continue"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete('button[title="Confirm"]');
    await I.waitForText('Your cases have been updated', 20);
    await I.see('Your cases have been updated');
    await I.click('//a[normalize-space()="Sign out"]');

};
