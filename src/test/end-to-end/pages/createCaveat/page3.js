'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    I.waitForText(createCaveatConfig.page3_waitForText, testConfig.TestTimeToWaitForText);

    if (crud === 'create') {
        I.fillField('#caveatorForenames', createCaveatConfig.page3_caveator_forenames);
        I.fillField('#caveatorSurname', createCaveatConfig.page3_caveator_surname);

        I.fillField('#caveatorEmailAddress', createCaveatConfig.page3_caveator_email);

        I.click(createCaveatConfig.UKpostcodeLink);
        I.fillField('#caveatorAddress_AddressLine1', createCaveatConfig.address_line1);
        I.fillField('#caveatorAddress_AddressLine2', createCaveatConfig.address_line2);
        I.fillField('#caveatorAddress_AddressLine3', createCaveatConfig.address_line3);
        I.fillField('#caveatorAddress_PostTown', createCaveatConfig.address_town);
        I.fillField('#caveatorAddress_County', createCaveatConfig.address_county);
        I.fillField('#caveatorAddress_PostCode', createCaveatConfig.address_postcode);
        I.fillField('#caveatorAddress_Country', createCaveatConfig.address_country);
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
