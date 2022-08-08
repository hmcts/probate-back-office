'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const {getLocalSonarScannerExecutable} = require("sonarqube-scanner/dist/sonar-scanner-executable");


module.exports = async function (verifyShareCase) {
    const removeUserLinkPP1 = '//tbody/tr[1]/td[3]/a[1]';
    const I = this;
    await I.waitForText('Your cases', 20);
    await I.seeElement('//input[@id="select-'+caseRefNumber+'"]');
    //await I.logInfo(scenarioName, 'PP2 User verified shared caseRef: '+caseRef+'');
    await I.click('//input[@id="select-'+caseRefNumber+'"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete('#btn-share-button', testConfig.CreateCaseDelay);
    await I.click('.govuk-accordion__open-all');
    await I.click(removeUserLinkPP1);
    await I.waitForText('TO BE REMOVED',5);
  //  await I.seeElement('//span[@class="hmcts-badge hmcts-badge--red ng-star-inserted"]');
    await I.waitForNavigationToComplete('button[title="Continue"]');
   // await I.seeElement('//span[contains(text(),\'To be removed\')]');
    await I.waitForNavigationToComplete('button[title="Confirm"]');
    await I.waitForText('Your cases have been updated', 5);
    await I.click('//a[normalize-space()="Sign out"]');
   // await I.logInfo(scenarioName, 'PP1 User has been removed from caseRef: '+caseRef+'');






    /*await I.click('//span[normalize-space()="'+caseIdShareCase+'"]');
    await I.wait(3);
    await I.selectOption('//select[@id="next-step"]', 'Delete' );











    await I.waitForNavigationToComplete('button[type="submit"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
    await I.seeElement('//h2[normalize-space()="'+caseRef+'"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete(commonConfig.submitButton);
    await I.wait(testConfig.CreateCaseDelay);
    await I.see('Case '+caseRef+' has been updated with event: Delete');
    await I.wait(testConfig.CreateCaseDelay);
    await I.click('//a[normalize-space()="Sign out"]');


     */
};
