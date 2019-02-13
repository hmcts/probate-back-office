'use strict';

const testConfig = require('src/test/config');
const caseDetailsConfig = require('./caseDetailsConfig');


module.exports = function (caseRef, tabConfigFile, dataConfigFile) {

    const I = this;

    if (tabConfigFile.TestTimeToWaitForText) {
        I.waitForText(tabConfigFile.waitForText, testConfig.TestTimeToWaitForText);
    }

    I.see(caseRef);

    console.log('tabConfigFile.tabName>>>', tabConfigFile.tabName);

    I.click(tabConfigFile.tabName);

    tabConfigFile.fields.forEach(function (fieldName) {
        console.log('fields>>>', fieldName);

        I.see(fieldName);
    });

    tabConfigFile.dataKeys.forEach(function (dataKey) {
        console.log('fields value>>>', dataConfigFile[dataKey]);

        I.see(dataConfigFile[dataKey]);
    });

    if (tabConfigFile.nextStep) {
        I.selectOption('#next-step', tabConfigFile.nextStep);
        I.waitForNavigationToComplete(caseDetailsConfig.goButton);
    }
};
