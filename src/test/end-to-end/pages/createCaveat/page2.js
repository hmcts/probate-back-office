'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    I.waitForText(createCaveatConfig.page2_waitForText, testConfig.TestTimeToWaitForText);

    if (crud === 'create') {
        I.fillField('#deceasedForenames', createCaveatConfig.page2_forenames);
        I.fillField('#deceasedSurname', createCaveatConfig.page2_surname);

        I.fillField('#deceasedDateOfDeath-day', createCaveatConfig.page2_dateOfDeath_day);
        I.fillField('#deceasedDateOfDeath-month', createCaveatConfig.page2_dateOfDeath_month);
        I.fillField('#deceasedDateOfDeath-year', createCaveatConfig.page2_dateOfDeath_year);

        I.fillField('#deceasedDateOfBirth-day', createCaveatConfig.page2_dateOfBirth_day);
        I.fillField('#deceasedDateOfBirth-month', createCaveatConfig.page2_dateOfBirth_month);
        I.fillField('#deceasedDateOfBirth-year', createCaveatConfig.page2_dateOfBirth_year);

        I.click(`#deceasedAnyOtherNames-${createCaveatConfig.page2_hasAliasYes}`);

        let counter = 0;

        Object.keys(createCaveatConfig).forEach(function (value) {
            if (value.includes('page2_alias_')) {
                I.click(createCaveatConfig.page2_addAliasButton);
                I.fillField(`#deceasedFullAliasNameList_${counter}_FullAliasName`, createCaveatConfig[value]);
                counter += 1;
            }
        });

        I.click(createCaveatConfig.UKpostcodeLink);
        I.fillField('#deceasedAddress_AddressLine1', createCaveatConfig.address_line1);
        I.fillField('#deceasedAddress_AddressLine2', createCaveatConfig.address_line2);
        I.fillField('#deceasedAddress_AddressLine3', createCaveatConfig.address_line3);
        I.fillField('#deceasedAddress_PostTown', createCaveatConfig.address_town);
        I.fillField('#deceasedAddress_County', createCaveatConfig.address_county);
        I.fillField('#deceasedAddress_PostCode', createCaveatConfig.address_postcode);
        I.fillField('#deceasedAddress_Country', createCaveatConfig.address_country);
    }

    if (crud === 'update') {
//        I.fillField('#deceasedForenames', createCaveatConfig.page2_firstnames_update);
//        I.fillField('#deceasedSurname', createCaveatConfig.page2_lastnames_update);

//        I.fillField('#deceasedDateOfBirth-day', createCaveatConfig.page2_dateOfBirth_day_update);
//        I.fillField('#deceasedDateOfBirth-month', createCaveatConfig.page2_dateOfBirth_month_update);
//        I.fillField('#deceasedDateOfBirth-year', createCaveatConfig.page2_dateOfBirth_year_update);
    }

    I.click(commonConfig.continueButton);
};
