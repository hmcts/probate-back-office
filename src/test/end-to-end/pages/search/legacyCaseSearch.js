'use strict';

const testConfig = require('src/test/config.js');
const legacyCaseSearchConfig = require('./legacyCaseSearchConfig.json');

module.exports = function () {

    const I = this;
    I.waitForText(legacyCaseSearchConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.waitForNavigationToComplete(legacyCaseSearchConfig.continueBtn);
};