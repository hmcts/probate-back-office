'use strict';

const testConfig = require('src/test/config.js');
const legacyCaseSearch3Config = require('./legacyCaseSearch3Config.json');

module.exports = function (jurisdiction, caseType, event) {

    const I = this;
    I.waitForText(legacyCaseSearch3Config.waitForText, testConfig.TestTimeToWaitForText);
    I.seeInField(legacyCaseSearch3Config.legacyCaseType, "Legacy WILL");
    I.seeInField(legacyCaseSearch3Config.probateManId, "89849");
    I.seeInField(legacyCaseSearch3Config.fullName, "ALBERT LOWERY");
    I.click(legacyCaseSearch3Config.yesOption);
    I.wait(20);

    I.waitForNavigationToComplete(legacyCaseSearch3Config.continueBtn);
    I.wait(5);
    I.waitForText(legacyCaseSearch3Config.waitForText, testConfig.TestTimeToWaitForText);
    I.waitForNavigationToComplete(legacyCaseSearch3Config.previousBtn);
};