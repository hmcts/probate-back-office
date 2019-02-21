'use strict';

const testConfig = require('src/test/config');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, tabConfigFile, dataConfigFile, nextStep) {

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

    // const step = tabConfigFile.nextStep || nextStep;
    // if (step) {
    //     I.selectOption('#next-step', step);
    //     I.waitForNavigationToComplete(commonConfig.goButton);
    // }
};
