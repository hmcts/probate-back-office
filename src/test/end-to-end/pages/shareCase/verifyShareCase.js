'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (verifyShareCase) {
    const I = this;
    await I.waitForText('Your cases', 20);
    await I.seeElement('//input[@id="select-'+caseRefNumber+'"]');
    //await I.logInfo(scenarioName, 'PP2 User verified shared caseRef: '+caseRef+'');
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
