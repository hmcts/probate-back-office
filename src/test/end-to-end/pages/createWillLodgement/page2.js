'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig2 = require('./createWillLodgementFastConfig2');

module.exports = function () {

    const I = this;

    I.waitForText(createWillLodgementConfig2.page2_waitForText, testConfig.TestTimeToWaitForText);
    //  I.amOnPage(createWillLodgementConfig2.pageUrl);

    I.fillField('#deceasedForenames', createWillLodgementConfig2.page2_firstnames);
    I.fillField('#deceasedSurname', createWillLodgementConfig2.page2_lastnames);

    I.selectOption('#deceasedGender', createWillLodgementConfig2.page2_gender);

    I.fillField('#deceasedDateOfBirth-day', createWillLodgementConfig2.page2_dateOfBirth_day);
    I.fillField('#deceasedDateOfBirth-month', createWillLodgementConfig2.page2_dateOfBirth_month);
    I.fillField('#deceasedDateOfBirth-year', createWillLodgementConfig2.page2_dateOfBirth_year);

    I.fillField('#deceasedDateOfDeath-day', createWillLodgementConfig2.page2_dateOfDeath_day);
    I.fillField('#deceasedDateOfDeath-month', createWillLodgementConfig2.page2_dateOfDeath_month);
    I.fillField('#deceasedDateOfDeath-year', createWillLodgementConfig2.page2_dateOfDeath_year);

    I.fillField('#deceasedTypeOfDeath', createWillLodgementConfig2.page2_typeOfDeath);

    I.click(`#deceasedAnyOtherNames-${createWillLodgementConfig2.page2_hasAliasYes}`);

    let counter = 0;

    Object.keys(createWillLodgementConfig2).forEach(function (value) {
        if (value.includes('page2_alias_')) {
            I.click(createWillLodgementConfig2.page2_addAliasButton);
            I.fillField(`#deceasedFullAliasNameList_${counter}_FullAliasName`, createWillLodgementConfig2[value]);
            counter += 1;
        }
    });

    I.click(createWillLodgementConfig2.UKpostcodeLink);
    I.fillField('#AddressLine1', createWillLodgementConfig2.address_line1);
    I.fillField('#AddressLine2', createWillLodgementConfig2.address_line2);
    I.fillField('#AddressLine3', createWillLodgementConfig2.address_line3);
    I.fillField('#PostTown', createWillLodgementConfig2.address_town);
    I.fillField('#County', createWillLodgementConfig2.address_county);
    I.fillField('#PostCode', createWillLodgementConfig2.address_postcode);
    I.fillField('#Country', createWillLodgementConfig2.address_country);
    I.fillField('#deceasedEmailAddress', createWillLodgementConfig2.page2_email);

    I.click(createWillLodgementConfig2.continueButton);
};
