'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = async function () {

    const I = this;
    const locator = newCaseConfig.xuiCreateCaseLocator;
    await I.wait(testConfig.CreateCaseDelay);
    var numElementFound = await I.grabNumberOfVisibleElements(locator);
    if(numElementFound<=0){
        do{
            I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases`);

            const checkUrl = await tryTo(() => I.seeInCurrentUrl('/cases'));
            if (checkUrl === false){
                await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
            }
            await I.wait(testConfig.CreateCaseDelay);
            numElementFound = await I.grabNumberOfVisibleElements(locator);
        }while(numElementFound<=0);
    }
    await I.waitForText(newCaseConfig.waitForText, testConfig.WaitForTextTimeout);
    await I.rejectCookies();
    await I.waitForEnabled({css: locator});
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete(locator);
};
