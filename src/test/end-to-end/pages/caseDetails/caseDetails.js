'use strict';

module.exports = function (caseRef, tabConfigFile, dataConfigFile, nextStep, endState) {

    const I = this;

    if (tabConfigFile.TestTimeToWaitForText) {
        I.waitForText(tabConfigFile.waitForText, tabConfigFile.TestTimeToWaitForText);
    }

    if (tabConfigFile.testTimeToWaitForTab) {
        I.waitForText(tabConfigFile.tabName, tabConfigFile.testTimeToWaitForTab);
    }

    I.see(caseRef);
    I.click(tabConfigFile.tabName);

    tabConfigFile.fields.forEach(function (fieldName) {
        I.see(fieldName);
    });

    // If 'Event History' tab, then check Next Step (Event), End State, Summary and Comment
    if (tabConfigFile.tabName === 'Event History') {

        let eventSummaryPrefix = nextStep;

        eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

        I.see(nextStep);
        I.see(endState);
        I.see(eventSummaryPrefix + dataConfigFile.summary);
        I.see(eventSummaryPrefix + dataConfigFile.comment);

    } else {
        tabConfigFile.dataKeys.forEach(function (dataKey) {
            I.see(dataConfigFile[dataKey]);
        });
    }
};
