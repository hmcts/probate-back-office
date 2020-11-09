const caseProgressConfig = require('./caseProgressConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// deceased details
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('#deceasedForenames'); 
    await I.fillField('#deceasedForenames', caseProgressConfig.page4_deceasedFirstname);
    await I.fillField('#deceasedSurname', caseProgressConfig.page4_deceasedSurname);
    await I.fillField('#deceasedDateOfDeath-day', caseProgressConfig.page4_deathDay);
    await I.fillField('#deceasedDateOfDeath-month', caseProgressConfig.page4_deathMonth);
    await I.fillField('#deceasedDateOfDeath-year', caseProgressConfig.page4_deathYear);
    await I.fillField('#deceasedDateOfBirth-day', caseProgressConfig.page4_deceasedDobDay);
    await I.fillField('#deceasedDateOfBirth-month', caseProgressConfig.page4_deceasedDobMonth);
    await I.fillField('#deceasedDateOfBirth-year', caseProgressConfig.page4_deceasedDobYear);

    await I.waitForNavigationToComplete(commonConfig.continueButton);    
};
