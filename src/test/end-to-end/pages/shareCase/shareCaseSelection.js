'use strict';

const testConfig = require('src/test/config.js');
const {getLocalSonarScannerExecutable} = require("sonarqube-scanner/dist/sonar-scanner-executable");


module.exports = async function (shareCaseSelection) {

    const I = this;
 //   let caseRef = await I.grabTextFrom('//div[@class="column-one-half"]//ccd-case-header');
  //  console.log(caseRef);
  //  let caseRefNumber = caseRef.replace(/\D/g, '');
  //  console.log(caseRefNumber);
    //const caseRefNumber = parseInt(caseRef.match(/\d/g).join(''), 20);

    await I.wait(testConfig.CreateCaseDelay);
   // await I.click({xpath: '//a[normalize-space()="Case list"]'}, testConfig.WaitForTextTimeout || 60);
    await I.click('//a[normalize-space()="Case list"]');
    await I.wait(8);
    await I.selectOption('#wb-jurisdiction', '0');
    await I.selectOption('#wb-case-type','Grant of representation');
    await I.click("button[title='Apply filter']");



    await I.wait(4);


    const element = '#select-1658444310728442';
    //const element = '#select-'+caseRefNumber+'';
    const next = '.pagination-next';
    const searchCase= await I.dontSeeElement(element);

    const caseElement = await I.grabAttributeFromAll(element);
   console.log(caseElement);
   /*
        if (I.dontSeeCheckboxIsChecked(element)) {

        await I.wait(4);
       // await I.seeElement('//input[@id="select-'+caseRefNumber+'"]');
       await I.click(next);
       await I.wait(2);
    }

        else {
        await I.click(element);


    }




   /* while (I.dontSeeElement(element)){
        await I.click(next);

        if(I.seeElement(element)){
            await I.click(element);
        }
    }


    */

};

