'use strict';

module.exports = async function (caseRef, tabConfigFile, dataConfigFile, nextStep, endState) {

    const I = this;

    if (tabConfigFile.TestTimeToWaitForText) {
        await I.waitForText(tabConfigFile.waitForText, tabConfigFile.TestTimeToWaitForText);
    }

    if (tabConfigFile.testTimeToWaitForTab) {
        await I.waitForText(tabConfigFile.tabName, tabConfigFile.testTimeToWaitForTab);
    }

    await I.see(caseRef);
    await I.click(tabConfigFile.tabName);

    for (let i = 0; i < tabConfigFile.fields.length; i++) {
        // eslint-disable-next-line
        await I.see(tabConfigFile.fields[i]);
    }

    // If 'Event History' tab, then check Next Step (Event), End State, Summary and Comment
    if (tabConfigFile.tabName === 'Event History') {

        let eventSummaryPrefix = nextStep;

        eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

        await I.see(nextStep);
        await I.see(endState);
        await I.see(eventSummaryPrefix + dataConfigFile.summary);
        await I.see(eventSummaryPrefix + dataConfigFile.comment);

    } else {

        for (let i = 0; i < tabConfigFile.dataKeys.length; i++) {
            // eslint-disable-next-line
            await I.see(dataConfigFile(tabConfigFile.dataKeys[i]));
        }
    }
};