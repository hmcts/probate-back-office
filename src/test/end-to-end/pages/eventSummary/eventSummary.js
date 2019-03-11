'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, configFile) {

    const I = this;
    I.waitForText(configFile.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.fillField('#field-trigger-summary', configFile.summary);
    I.fillField('#field-trigger-description', configFile.comment);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
