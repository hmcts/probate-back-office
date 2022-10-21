'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRefNumber) {
    const I = this;
    await I.waitForText('Your cases', 20);
    await I.wait(4);
    await I.click('//div[normalize-space()="Case reference"]');
    await I.wait(2);
    await I.click('//input[@id="select-'+caseRefNumber+'"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete('#btn-share-button', 2);
    await I.click('.govuk-accordion__open-all');
    await I.wait(1);
    await I.click('Remove','//tr[contains(.,"probatesolicitortestorgtest1@gmail.com")]');
    await I.waitForText('TO BE REMOVED',5);
    await I.waitForNavigationToComplete('button[title="Continue"]');
    await I.waitForNavigationToComplete('button[title="Confirm"]');
    await I.waitForText('Your cases have been updated', 5);
    await I.click('//a[normalize-space()="Sign out"]');

};
