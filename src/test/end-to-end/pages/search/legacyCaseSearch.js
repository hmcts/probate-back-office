'use strict';

const testConfig = require('src/test/config.js');
const legacyCaseSearchConfig = require('./legacyCaseSearchConfig.json');

module.exports = function (jurisdiction, caseType, event) {

    const I = this;
    I.waitForText(legacyCaseSearchConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.wait(20);

    I.waitForNavigationToComplete(legacyCaseSearchConfig.continueBtn);
};