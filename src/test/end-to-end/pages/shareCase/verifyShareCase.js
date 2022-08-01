'use strict';

const testConfig = require('src/test/config.js');
const {getLocalSonarScannerExecutable} = require("sonarqube-scanner/dist/sonar-scanner-executable");


module.exports = async function (verifyShareCase) {

    const I = this;
    await I.wait(6);
    console.log(caseRefNumber);
    await I.seeElement('//input[@id="select-'+caseRefNumber+'"]');
    await I.click('//span[normalize-space()="'+caseIdShareCase+'"]');
    await I.wait(4);
    await I.selectOption('//select[@id="next-step"]', 'Delete' );
    await I.click('button[type="submit"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.click('//button[normalize-space()="Continue"]');
    await I.seeElement('//h2[normalize-space()="'+caseRef+'"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.click('//button[normalize-space()="Submit"]');
    await I.wait(testConfig.CreateCaseDelay);
    await I.see('Case '+caseRef+' has been updated with event: Delete');
    await I.wait(5);
    await I.click('//a[normalize-space()="Sign out"]');







};
