'use strict';

const testConfig = require('src/test/config.cjs');

module.exports = async function (caseRef, tabConfigFile, dataConfigFile) {
    const I = this;
    const delay = testConfig.CaseDetailsDelayDefault;

    if (tabConfigFile.tabName) {
        const tabXPath = `//div[contains(text(),"${tabConfigFile.tabName}")]`;
        // Tabs are hidden when there are more tabs
        await I.waitForElement(tabXPath, tabConfigFile.testTimeToWaitForTab || 60);
    }

    await I.waitForText(caseRef, testConfig.WaitForTextTimeout || 60);

    await I.clickTab(tabConfigFile.tabName);
    await I.wait(delay);
    await I.runAccessibilityTest();

    if (tabConfigFile.waitForText) {
        await I.waitForText(tabConfigFile.waitForText, testConfig.WaitForTextTimeout || 60);
    }

    /* eslint-disable no-await-in-loop */
    for (let i = 0; i < tabConfigFile.fields.length; i++) {
        if (tabConfigFile.fields[i] && tabConfigFile.fields[i] !== '') {
            await I.see(tabConfigFile.fields[i]);
        }
    }

    for (let i = 0; i < tabConfigFile.dataKeysBilingual.length; i++) {
        await I.waitForText(dataConfigFile[tabConfigFile.dataKeysBilingual[i]], testConfig.WaitForTextTimeout || 60);
    }
};
