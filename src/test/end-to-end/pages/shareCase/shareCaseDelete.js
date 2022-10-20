'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (shareCaseDelete) {
    const I = this;
    await I.waitForText('Your cases', 20);
    await I.click('//span[normalize-space()="'+caseIdShareCase+'"]');
    await I.wait(3);
    await I.selectOption('//select[@id="next-step"]', 'Delete' );
    await I.waitForNavigationToComplete('button[type="submit"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
    await I.seeElement('//h2[normalize-space()="'+caseRef+'"]');
    await I.wait(2);
    await I.waitForNavigationToComplete(commonConfig.submitButton);
    await I.wait(2);
    await I.see('Case '+caseRef+' has been updated with event: Delete');
    await I.wait(testConfig.CreateCaseDelay);
    await I.click('//a[normalize-space()="Sign out"]');

};
