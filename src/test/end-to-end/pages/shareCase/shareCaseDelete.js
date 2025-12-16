'use strict';

const testConfig = require('src/test/config.cjs');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseIdShareCase, caseRef) {
    const I = this;
    await I.waitForText('Your cases', 20);
    const caseRefNoDashes = await I.replaceAll(caseIdShareCase, '-', '');
    I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/PROBATE/GrantOfRepresentation/${caseRefNoDashes}`);
    await I.wait(testConfig.ManualDelayMedium);
    await I.selectOption('//select[@id="next-step"]', 'Delete');
    await I.waitForNavigationToComplete('button[type="submit"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
    await I.seeElement('//h2[normalize-space()="'+caseRef+'"]');
    await I.wait(2);
    await I.waitForNavigationToComplete(commonConfig.submitButton);
    await I.wait(2);
    await I.see('Case ' + caseRef + ' has been updated with event: Delete');
    await I.wait(testConfig.CreateCaseDelay);
    await I.click('//a[normalize-space()="Sign out"]');

};
