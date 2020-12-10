'use strict';

const intestacyDetailsConfig = require('./intestacyDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsMinorityInterest');
    await I.click(`#solsMinorityInterest-${intestacyDetailsConfig.optionNo}`);
    await I.click(`#immovableEstate-${intestacyDetailsConfig.optionYes}`);
    await I.click(`#solsApplicantSiblings-${intestacyDetailsConfig.optionNo}`);


    await I.fillField('#primaryApplicantForenames', intestacyDetailsConfig.page2_firstname);
    await I.fillField('#primaryApplicantSurname', intestacyDetailsConfig.page2_lastname);
    await I.click(intestacyDetailsConfig.UKpostcodeLink);

    await I.fillField('#primaryApplicantAddress_AddressLine1', intestacyDetailsConfig.address_line1);
    await I.fillField('#primaryApplicantAddress_AddressLine2', intestacyDetailsConfig.address_line2);
    await I.fillField('#primaryApplicantAddress_AddressLine3', intestacyDetailsConfig.address_line3);
    await I.fillField('#primaryApplicantAddress_PostTown', intestacyDetailsConfig.address_town);
    await I.fillField('#primaryApplicantAddress_County', intestacyDetailsConfig.address_county);
    await I.fillField('#primaryApplicantAddress_PostCode', intestacyDetailsConfig.address_postcode);
    await I.fillField('#primaryApplicantAddress_Country', intestacyDetailsConfig.address_country);

    await I.fillField('#primaryApplicantPhoneNumber', intestacyDetailsConfig.page2_phone);
    await I.fillField('#primaryApplicantEmailAddress', intestacyDetailsConfig.page2_email);

    await I.selectOption('#deceasedMaritalStatus', 'Never married');
    pause();
    await I.click(intestacyDetailsConfig.UKpostcodeLink);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
