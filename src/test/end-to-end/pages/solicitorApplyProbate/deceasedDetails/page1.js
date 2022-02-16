'use strict';

const deceasedDetailsConfig = require('./deceasedDetailsConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (deathTypeDate) {
    const I = this;
    await I.waitForElement('#deceasedForenames');
    await I.runAccessibilityTest();
    await I.fillField('#deceasedForenames', deceasedDetailsConfig.page1_forenames);
    await I.fillField('#deceasedSurname', deceasedDetailsConfig.page1_surname);

    await I.fillField('#deceasedDateOfBirth-day', deceasedDetailsConfig.page1_dateOfBirth_day);
    await I.fillField('#deceasedDateOfBirth-month', deceasedDetailsConfig.page1_dateOfBirth_month);
    await I.fillField('#deceasedDateOfBirth-year', deceasedDetailsConfig.page1_dateOfBirth_year);

    if (deathTypeDate === 'EE') {
        await I.fillField('#deceasedDateOfDeath-day', deceasedDetailsConfig.page1_dateOfDeath_dayEE);
        await I.fillField('#deceasedDateOfDeath-month', deceasedDetailsConfig.page1_dateOfDeath_monthEE);
        await I.fillField('#deceasedDateOfDeath-year', deceasedDetailsConfig.page1_dateOfDeath_yearEE);
    } else {
        await I.fillField('#deceasedDateOfDeath-day', deceasedDetailsConfig.page1_dateOfDeath_day);
        await I.fillField('#deceasedDateOfDeath-month', deceasedDetailsConfig.page1_dateOfDeath_month);
        await I.fillField('#deceasedDateOfDeath-year', deceasedDetailsConfig.page1_dateOfDeath_year);
    }

    await I.click(`#deceasedDomicileInEngWales_${deceasedDetailsConfig.optionYes}`);

    await I.click(deceasedDetailsConfig.UKpostcodeLink);

    await I.fillField('#deceasedAddress__detailAddressLine1', deceasedDetailsConfig.address_line1);
    await I.fillField('#deceasedAddress__detailAddressLine2', deceasedDetailsConfig.address_line2);
    await I.fillField('#deceasedAddress__detailAddressLine3', deceasedDetailsConfig.address_line3);
    await I.fillField('#deceasedAddress__detailPostTown', deceasedDetailsConfig.address_town);
    await I.fillField('#deceasedAddress__detailCounty', deceasedDetailsConfig.address_county);
    await I.fillField('#deceasedAddress__detailPostCode', deceasedDetailsConfig.address_postcode);
    await I.fillField('#deceasedAddress__detailCountry', deceasedDetailsConfig.address_country);

    await I.click(`#deceasedAnyOtherNames_${deceasedDetailsConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
