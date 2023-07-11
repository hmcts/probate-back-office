'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = async function () {

    const I = this;
    const locator = newCaseConfig.xuiCreateCaseLocator;
    await I.wait(testConfig.CreateCaseDelay);
    var numElementFound = 0;
    do{
        I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases`);
        await I.wait(testConfig.CreateCaseDelay);
        const currentPageURL = I.seeInCurrentUrl('/cases');

        if (!currentPageURL){
            await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
        }
        await I.wait(testConfig.CreateCaseDelay);
        numElementFound = await I.grabNumberOfVisibleElements(locator);
    }while(numElementFound<=0);
    await I.waitForText(newCaseConfig.waitForText, testConfig.WaitForTextTimeout);
    await I.rejectCookies();
    await I.waitForEnabled({css: locator});
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForNavigationToComplete(locator);
};
