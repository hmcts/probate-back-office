'use strict';

const testConfig = require('src/test/config.js');
const {getLocalSonarScannerExecutable} = require("sonarqube-scanner/dist/sonar-scanner-executable");


module.exports = async function (shareCaseSelection) {

    const I = this;
    let caseRef = await I.grabTextFrom('//div[@class="column-one-half"]//ccd-case-header');
    console.log(caseRef);
    let caseRefNumber = caseRef.replace(/\D/g, '');
    console.log(caseRefNumber);
    //const caseRefNumber = parseInt(caseRef.match(/\d/g).join(''), 20);

    await I.wait(testConfig.CreateCaseDelay);
   // await I.click({xpath: '//a[normalize-space()="Case list"]'}, testConfig.WaitForTextTimeout || 60);
    await I.click('//a[normalize-space()="Case list"]');
    await I.wait(4);
    await I.selectOption('#wb-jurisdiction', '0');
    await I.selectOption('#wb-case-type','Grant of representation');
    await I.click("button[title='Apply filter']");


        await I.wait(4);
        await I.click('//input[@id="select-'+caseRefNumber+'"]');
        await I.wait(2);
        await I.click('#btn-share-button');
        await I.wait(testConfig.CreateCaseDelay);
        await I.fillField('input[role="combobox"]', 'PP');
        await I.wait(testConfig.CreateCaseDelay);
        await I.click('//span[normalize-space()="ProbatePPTwo Org2 - probate.pp2.org2@gmail.com"]');
        await I.click('#btn-add-user');
        await I.click('.govuk-accordion__open-all');
        await I.see('probate.pp2.org2@gmail.com');
        await I.see('TO BE ADDED');
        await I.click('button[title="Continue"]');
        await I.wait(testConfig.CreateCaseDelay);
        await I.click('button[title="Confirm"]');
        await I.wait(testConfig.CreateCaseDelay);
        await I.see('Your cases have been updated');
        await I.click('//a[normalize-space()="Sign out"]');










   /* while (I.dontSeeElement(element)){
        await I.click(next);

        if(I.seeElement(element)){
            await I.click(element);
        }
    }


    */

};

