'use strict';

const testConfig = require('src/test/config');

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

    if (nextStep) {
        I.see(nextStep);
    }

    if (endState) {
        I.see(endState);
    }

    tabConfigFile.fields.forEach(function (fieldName) {
        I.see(fieldName);
    });

    tabConfigFile.dataKeys.forEach(function (dataKey) {
        I.see(dataConfigFile[dataKey]);
    });
};
