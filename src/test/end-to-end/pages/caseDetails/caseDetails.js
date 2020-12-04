'use strict';

module.exports = async function (caseRef, tabConfigFile, dataConfigFile, nextStep, endState) {

    const I = this;

    if (tabConfigFile.tabName) {
        await I.waitForText(tabConfigFile.tabName, tabConfigFile.testTimeToWaitForTab || 60);
    }

    await I.waitForText(caseRef);
    await I.waitForText(tabConfigFile.tabName);
    await I.click(tabConfigFile.tabName);

    if (tabConfigFile.waitForText) {
        await I.waitForText(tabConfigFile.waitForText, tabConfigFile.TestTimeToWaitForText || 60);
    }

    for (let i = 0; i < tabConfigFile.fields.length; i++) {
        // eslint-disable-next-line
        await I.waitForText(tabConfigFile.fields[i]);
        // await I.see(tabConfigFile.fields[i]);
    }

    // If 'Event History' tab, then check Next Step (Event), End State, Summary and Comment
    if (tabConfigFile.tabName === 'Event History') {

        let eventSummaryPrefix = nextStep;

        eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

        await I.waitForText(nextStep);
        await I.waitForText(endState);
        await I.waitForText(eventSummaryPrefix + dataConfigFile.summary);
        await I.waitForText(eventSummaryPrefix + dataConfigFile.comment);

    } else {

        for (let i = 0; i < tabConfigFile.dataKeys.length; i++) {
            // eslint-disable-next-line
            await I.waitForText(dataConfigFile[tabConfigFile.dataKeys[i]]);
        }
    }
};
