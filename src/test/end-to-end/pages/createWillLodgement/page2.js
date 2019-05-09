'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig = require('./createWillLodgementConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createWillLodgementConfig.page2_waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#deceasedForenames', createWillLodgementConfig.page2_forenames);
        I.fillField('#deceasedSurname', createWillLodgementConfig.page2_surname);

        I.selectOption('#deceasedGender', createWillLodgementConfig.page2_gender);

        I.fillField('#deceasedDateOfBirth-day', createWillLodgementConfig.page2_dateOfBirth_day);
        I.fillField('#deceasedDateOfBirth-month', createWillLodgementConfig.page2_dateOfBirth_month);
        I.fillField('#deceasedDateOfBirth-year', createWillLodgementConfig.page2_dateOfBirth_year);

        I.fillField('#deceasedDateOfDeath-day', createWillLodgementConfig.page2_dateOfDeath_day);
        I.fillField('#deceasedDateOfDeath-month', createWillLodgementConfig.page2_dateOfDeath_month);
        I.fillField('#deceasedDateOfDeath-year', createWillLodgementConfig.page2_dateOfDeath_year);

        I.fillField('#deceasedTypeOfDeath', createWillLodgementConfig.page2_typeOfDeath);

        I.click(`#deceasedAnyOtherNames-${createWillLodgementConfig.page2_hasAliasYes}`);

        let counter = 0;

        Object.keys(createWillLodgementConfig).forEach(function (value) {
            if (value.includes('page2_alias_')) {
                I.click(createWillLodgementConfig.page2_addAliasButton);
                I.fillField(`#deceasedFullAliasNameList_${counter}_FullAliasName`, createWillLodgementConfig[value]);
                counter += 1;
            }
        });

        I.click(createWillLodgementConfig.UKpostcodeLink);
        I.fillField('#deceasedAddress__AddressLine1', createWillLodgementConfig.address_line1);
        I.fillField('#deceasedAddress__AddressLine2', createWillLodgementConfig.address_line2);
        I.fillField('#deceasedAddress__AddressLine3', createWillLodgementConfig.address_line3);
        I.fillField('#deceasedAddress__PostTown', createWillLodgementConfig.address_town);
        I.fillField('#deceasedAddress__County', createWillLodgementConfig.address_county);
        I.fillField('#deceasedAddress__PostCode', createWillLodgementConfig.address_postcode);
        I.fillField('#deceasedAddress__Country', createWillLodgementConfig.address_country);
        I.fillField('#deceasedEmailAddress', createWillLodgementConfig.page2_email);
    }

    if (crud === 'update') {
        I.waitForText(createWillLodgementConfig.page2_amend_waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#deceasedForenames', createWillLodgementConfig.page2_forenames_update);
        I.fillField('#deceasedSurname', createWillLodgementConfig.page2_surname_update);

        I.fillField('#deceasedDateOfBirth-day', createWillLodgementConfig.page2_dateOfBirth_day_update);
        I.fillField('#deceasedDateOfBirth-month', createWillLodgementConfig.page2_dateOfBirth_month_update);
        I.fillField('#deceasedDateOfBirth-year', createWillLodgementConfig.page2_dateOfBirth_year_update);
    }

    I.click(commonConfig.continueButton);
};
