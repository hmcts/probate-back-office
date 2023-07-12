'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig');

module.exports = async function (caseType, event) {

    const I = this;
    await I.wait(testConfig.CreateCaseDelay);
    var numElementFound = await I.grabNumberOfVisibleElements({css: `#cc-jurisdiction option[value=PROBATE]`});
    if(numElementFound<=0) {
        do {
            I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-filter`);

            const checkUrl = await tryTo(() => I.seeInCurrentUrl('/cases/case-filter'));
            if (checkUrl === false){
                await I.refreshCreateCasePage(true, testConfig.CaseProgressSignInDelay);
            }
            await I.wait(testConfig.CreateCaseDelay);
            numElementFound = await I.grabNumberOfVisibleElements({css: `#cc-jurisdiction option[value=PROBATE]`});
        } while (numElementFound <= 0)
    }
    await I.waitForEnabled({css: '#cc-jurisdiction'}, testConfig.WaitForTextTimeout || 60);
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForElement({css: '#cc-jurisdiction option[value=PROBATE]'}, testConfig.WaitForTextTimeout || 60);
    await I.selectOption('#cc-jurisdiction', 'PROBATE');
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForEnabled({css: '#cc-case-type'}, testConfig.WaitForTextTimeout || 60);
    await I.retry(5).selectOption('#cc-case-type', caseType);
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForEnabled({css: '#cc-event'}, testConfig.WaitForTextTimeout || 60);
    await I.retry(5).selectOption('#cc-event', event);
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForEnabled(createCaseConfig.startButton, testConfig.WaitForTextTimeout || 60);
    await I.waitForNavigationToComplete(createCaseConfig.startButton);
    await I.wait(testConfig.CreateCaseDelay);
};
