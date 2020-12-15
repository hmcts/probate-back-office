'use strict';

const admonWillDetailsConfig = require('./admonWillDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (updateAddressManually) {
    const I = this;
    await I.waitForElement('#solsEntitledMinority');
    await I.click(`#solsEntitledMinority-${admonWillDetailsConfig.optionNo}`);
    await I.click(`#immovableEstate-${admonWillDetailsConfig.optionYes}`);
    await I.click(`#solsDiedOrNotApplying-${admonWillDetailsConfig.optionYes}`);
    await I.click(`#solsResiduary-${admonWillDetailsConfig.optionYes}`);
    await I.waitForElement('#solsResiduaryType');
    await I.selectOption('#solsResiduaryType', admonWillDetailsConfig.page2_legateeAndDevisee);
    await I.click(`#solsLifeInterest-${admonWillDetailsConfig.optionNo}`);

    await I.fillField('#primaryApplicantForenames', admonWillDetailsConfig.applicant_firstname);
    await I.fillField('#primaryApplicantSurname', admonWillDetailsConfig.applicant_lastname);

    if (updateAddressManually) {
        await I.click(admonWillDetailsConfig.UKpostcodeLink);
    }

    await I.fillField('#primaryApplicantAddress_AddressLine1', admonWillDetailsConfig.address_line1);
    await I.fillField('#primaryApplicantAddress_AddressLine2', admonWillDetailsConfig.address_line2);
    await I.fillField('#primaryApplicantAddress_AddressLine3', admonWillDetailsConfig.address_line3);
    await I.fillField('#primaryApplicantAddress_PostTown', admonWillDetailsConfig.address_town);
    await I.fillField('#primaryApplicantAddress_County', admonWillDetailsConfig.address_county);
    await I.fillField('#primaryApplicantAddress_PostCode', admonWillDetailsConfig.address_postcode);
    await I.fillField('#primaryApplicantAddress_Country', admonWillDetailsConfig.address_country);

    await I.fillField('#primaryApplicantPhoneNumber', admonWillDetailsConfig.applicant_phone);
    await I.fillField('#primaryApplicantEmailAddress', admonWillDetailsConfig.applicant_email);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
