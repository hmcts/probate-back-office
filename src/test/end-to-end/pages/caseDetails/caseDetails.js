'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (caseRef, tabConfigFile, dataConfigFile, nextStep, endState, delay = testConfig.CaseDetailsDelayDefault) {
    const I = this;

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

    const dataConfigKeys = tabConfigFile.dataKeys;
    // If 'Event History' tab, then check Next Step (Event), End State, Summary and Comment
    if (tabConfigFile.tabName === 'Event History') {
        I.see(nextStep);
        I.see(endState);

        let eventSummaryPrefix = nextStep;

        eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

        await I.waitForText(nextStep, testConfig.WaitForTextTimeout || 60);
        await I.waitForText(endState, testConfig.WaitForTextTimeout || 60);

        if (dataConfigKeys) {
            await I.waitForText(eventSummaryPrefix + dataConfigFile.summary, testConfig.WaitForTextTimeout || 60);
            await I.waitForText(eventSummaryPrefix + dataConfigFile.comment, testConfig.WaitForTextTimeout || 60);
        }

    } else if (dataConfigKeys) {
        for (let i = 0; i < tabConfigFile.dataKeys.length; i++) {
            await I.waitForText(dataConfigFile[tabConfigFile.dataKeys[i]], testConfig.WaitForTextTimeout || 60);
        }
    }
};
