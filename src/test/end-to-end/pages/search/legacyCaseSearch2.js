'use strict';

const testConfig = require('src/test/config.js');
const legacyCaseSearch2Config = require('./legacyCaseSearch2Config.json');

module.exports = function (jurisdiction, caseType, event) {

    const I = this;
    I.waitForText(legacyCaseSearch2Config.waitForText, testConfig.TestTimeToWaitForText);
//    I.fillField(legacyCaseSearch2Config.probateManId, "Albert");
    I.fillField(legacyCaseSearch2Config.forename, "Albert");
//    I.fillField(legacyCaseSearch2Config.surname, "Surname");
//    I.fillField(legacyCaseSearch2Config.deceasedDayOB, "01");
//    I.fillField(legacyCaseSearch2Config.deceasedMonthOB, "01");
//    I.fillField(legacyCaseSearch2Config.deceasedYearOB, "1930");
//    I.fillField(legacyCaseSearch2Config.deceasedDayOD, "01");
//    I.fillField(legacyCaseSearch2Config.deceasedMonthOD, "01");
//    I.fillField(legacyCaseSearch2Config.deceasedYearOD, "2017");

    I.wait(20);

    I.waitForNavigationToComplete(legacyCaseSearch2Config.continueBtn);
    I.wait(20);
};