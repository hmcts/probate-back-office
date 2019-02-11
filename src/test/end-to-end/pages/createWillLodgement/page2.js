'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig = require('./createWillLodgementConfig');

module.exports = function () {

    const I = this;

    I.waitForText(createWillLodgementConfig.page2_waitForText, testConfig.TestTimeToWaitForText);

    I.fillField('#deceasedForenames', createWillLodgementConfig.page2_firstnames);
    I.fillField('#deceasedSurname', createWillLodgementConfig.page2_lastnames);

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
    I.fillField('#AddressLine1', createWillLodgementConfig.address_line1);
    I.fillField('#AddressLine2', createWillLodgementConfig.address_line2);
    I.fillField('#AddressLine3', createWillLodgementConfig.address_line3);
    I.fillField('#PostTown', createWillLodgementConfig.address_town);
    I.fillField('#County', createWillLodgementConfig.address_county);
    I.fillField('#PostCode', createWillLodgementConfig.address_postcode);
    I.fillField('#Country', createWillLodgementConfig.address_country);
    I.fillField('#deceasedEmailAddress', createWillLodgementConfig.page2_email);

    I.click(createWillLodgementConfig.continueButton);
};
