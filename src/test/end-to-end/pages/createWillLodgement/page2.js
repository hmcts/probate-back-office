'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig = require('./createWillLodgementConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud, unique_deceased_user) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createWillLodgementConfig.page2_waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#deceasedForenames', createWillLodgementConfig.page2_forenames + '_' + unique_deceased_user);
        I.fillField('#deceasedSurname', createWillLodgementConfig.page2_surname + '_' + unique_deceased_user);

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
        I.fillField('#deceasedFullAliasNameList_0_FullAliasName', createWillLodgementConfig.page2_alias_1 + '_' + unique_deceased_user);

        I.click(createWillLodgementConfig.UKpostcodeLink);
        I.fillField('#deceasedAddress_AddressLine1', createWillLodgementConfig.address_line1);
        I.fillField('#deceasedAddress_AddressLine2', createWillLodgementConfig.address_line2);
        I.fillField('#deceasedAddress_AddressLine3', createWillLodgementConfig.address_line3);
        I.fillField('#deceasedAddress_PostTown', createWillLodgementConfig.address_town);
        I.fillField('#deceasedAddress_County', createWillLodgementConfig.address_county);
        I.fillField('#deceasedAddress_PostCode', createWillLodgementConfig.address_postcode);
        I.fillField('#deceasedAddress_Country', createWillLodgementConfig.address_country);
        I.fillField('#deceasedEmailAddress', createWillLodgementConfig.page2_email);
    }

    if (crud === 'update') {
        I.waitForText(createWillLodgementConfig.page2_amend_waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#deceasedForenames', createWillLodgementConfig.page2_forenames + '_' + unique_deceased_user + ' ' + 'UPDATED' + unique_deceased_user);
        I.fillField('#deceasedSurname', createWillLodgementConfig.page2_surname + '_' + unique_deceased_user + ' ' + 'UPDATED' + unique_deceased_user);
        I.fillField('#deceasedFullAliasNameList_0_FullAliasName', createWillLodgementConfig.page2_alias_1 + '_' + unique_deceased_user + '_' + 'UPDATED' + unique_deceased_user);

        I.fillField('#deceasedDateOfDeath-day', createWillLodgementConfig.page2_dateOfDeath_day_update);
        I.fillField('#deceasedDateOfDeath-month', createWillLodgementConfig.page2_dateOfDeath_month_update);
        I.fillField('#deceasedDateOfDeath-year', createWillLodgementConfig.page2_dateOfDeath_year_update);
        I.fillField('#deceasedDateOfBirth-day', createWillLodgementConfig.page2_dateOfBirth_day_update);
        I.fillField('#deceasedDateOfBirth-month', createWillLodgementConfig.page2_dateOfBirth_month_update);
        I.fillField('#deceasedDateOfBirth-year', createWillLodgementConfig.page2_dateOfBirth_year_update);
    }

    if (crud === 'update2orig') {

        // "reverting" update back to defaults - to enable case-match with matching case
        I.waitForNavigationToComplete(commonConfig.continueButton);
        I.waitForText(createWillLodgementConfig.page2_amend_waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#deceasedDateOfDeath-day', createWillLodgementConfig.page2_dateOfDeath_day);
        I.fillField('#deceasedDateOfDeath-month', createWillLodgementConfig.page2_dateOfDeath_month);
        I.fillField('#deceasedDateOfDeath-year', createWillLodgementConfig.page2_dateOfDeath_year);
        I.fillField('#deceasedDateOfBirth-day', createWillLodgementConfig.page2_dateOfBirth_day);
        I.fillField('#deceasedDateOfBirth-month', createWillLodgementConfig.page2_dateOfBirth_month);
        I.fillField('#deceasedDateOfBirth-year', createWillLodgementConfig.page2_dateOfBirth_year);
        I.waitForNavigationToComplete(commonConfig.continueButton);
    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
