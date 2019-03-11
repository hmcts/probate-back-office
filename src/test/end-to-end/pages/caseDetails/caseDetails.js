'use strict';

const testConfig = require('src/test/config');

module.exports = function (caseRef, tabConfigFile, dataConfigFile) {

    const I = this;

    if (tabConfigFile.TestTimeToWaitForText) {
        I.waitForText(tabConfigFile.waitForText, testConfig.TestTimeToWaitForText);
    }

    I.see(caseRef);
    I.click(tabConfigFile.tabName);

    tabConfigFile.fields.forEach(function (fieldName) {
        I.see(fieldName);
    });

    tabConfigFile.dataKeys.forEach(function (dataKey) {
        I.see(dataConfigFile[dataKey]);
    });
};
