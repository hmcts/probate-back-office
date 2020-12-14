'use strict';

const deceasedDetailsConfig = require('./deceasedDetailsConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#deceasedForenames');
    await I.fillField('#deceasedForenames', deceasedDetailsConfig.page1_forenames);
    await I.fillField('#deceasedSurname', deceasedDetailsConfig.page1_surname);

    await I.fillField('#deceasedDateOfDeath-day', deceasedDetailsConfig.page1_dateOfDeath_day);
    await I.fillField('#deceasedDateOfDeath-month', deceasedDetailsConfig.page1_dateOfDeath_month);
    await I.fillField('#deceasedDateOfDeath-year', deceasedDetailsConfig.page1_dateOfDeath_year);

    await I.fillField('#deceasedDateOfBirth-day', deceasedDetailsConfig.page1_dateOfBirth_day);
    await I.fillField('#deceasedDateOfBirth-month', deceasedDetailsConfig.page1_dateOfBirth_month);
    await I.fillField('#deceasedDateOfBirth-year', deceasedDetailsConfig.page1_dateOfBirth_year);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
