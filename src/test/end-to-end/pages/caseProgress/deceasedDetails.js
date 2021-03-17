'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// deceased details
module.exports = async function (caseProgressConfig) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('#deceasedForenames');
    await I.fillField('#deceasedForenames', caseProgressConfig.deceasedFirstname);
    await I.fillField('#deceasedSurname', caseProgressConfig.deceasedSurname);
    await I.fillField('#deceasedDateOfDeath-day', caseProgressConfig.deathDay);
    await I.fillField('#deceasedDateOfDeath-month', caseProgressConfig.deathMonth);
    await I.fillField('#deceasedDateOfDeath-year', caseProgressConfig.deathYear);
    await I.fillField('#deceasedDateOfBirth-day', caseProgressConfig.deceasedDobDay);
    await I.fillField('#deceasedDateOfBirth-month', caseProgressConfig.deceasedDobMonth);
    await I.fillField('#deceasedDateOfBirth-year', caseProgressConfig.deceasedDobYear);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
