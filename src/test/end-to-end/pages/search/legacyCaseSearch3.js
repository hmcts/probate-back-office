'use strict';

const testConfig = require('src/test/config.js');
const legacyCaseSearch3Config = require('./legacyCaseSearch3Config.json');

module.exports = function () {

    const I = this;
    I.waitForText(legacyCaseSearch3Config.waitForText, testConfig.TestTimeToWaitForText);
//    I.seeInField(legacyCaseSearch3Config.legacyCaseType, "Legacy LEGACY APPLICATION");
    I.seeInField(legacyCaseSearch3Config.legacyCaseType, "Legacy CAVEAT");
    I.seeInField(legacyCaseSearch3Config.fullName, "ROBERT SMITH WILIE");
    I.click(legacyCaseSearch3Config.yesOption);

    I.waitForNavigationToComplete(legacyCaseSearch3Config.continueBtn);
    I.waitForText(legacyCaseSearch3Config.waitForText, testConfig.TestTimeToWaitForText);
    I.waitForNavigationToComplete(legacyCaseSearch3Config.previousBtn);
};