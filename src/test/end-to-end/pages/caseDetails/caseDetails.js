'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (caseRef, tabConfigFile, dataConfigFile, nextStep, endState) {

    const I = this;

    if (tabConfigFile.tabName) {
        await I.waitForText(tabConfigFile.tabName, tabConfigFile.testTimeToWaitForTab || 60);
    }

    await I.waitForText(caseRef, testConfig.TestTimeToWaitForText || 60);
    await I.waitForText(tabConfigFile.tabName, testConfig.TestTimeToWaitForText || 60);

    await I.clickTab(tabConfigFile.tabName);

    if (tabConfigFile.waitForText) {
        await I.waitForText(tabConfigFile.waitForText, testConfig.TestTimeToWaitForText || 60);
    }

    for (let i = 0; i < tabConfigFile.fields.length; i++) {
        // eslint-disable-next-line
        await I.waitForText(tabConfigFile.fields[i]);
        // await I.see(tabConfigFile.fields[i]);
    }

    const dataConfigKeys = tabConfigFile.dataKeys;
    // If 'Event History' tab, then check Next Step (Event), End State, Summary and Comment
    if (tabConfigFile.tabName === 'Event History') {
        I.see(nextStep);
        I.see(endState);

        let eventSummaryPrefix = nextStep;

        eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

        await I.waitForText(nextStep, testConfig.TestTimeToWaitForText || 60);
        await I.waitForText(endState, testConfig.TestTimeToWaitForText || 60);

        if (dataConfigKeys) {
            await I.waitForText(eventSummaryPrefix + dataConfigFile.summary, testConfig.TestTimeToWaitForText || 60);
            await I.waitForText(eventSummaryPrefix + dataConfigFile.comment, testConfig.TestTimeToWaitForText || 60);
        }

    } else if (dataConfigKeys) {
        for (let i = 0; i < tabConfigFile.dataKeys.length; i++) {
            // eslint-disable-next-line
            await I.waitForText(dataConfigFile[tabConfigFile.dataKeys[i]], testConfig.TestTimeToWaitForText || 60);
        }
    }
};
