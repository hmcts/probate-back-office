'use strict';

const admonWillDetailsConfig = require('./admonWillDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (updateAddressManually) {
    const I = this;
    await I.waitForText('First name(s)');
    await I.runAccessibilityTest();

    await I.fillField('#primaryApplicantForenames', admonWillDetailsConfig.applicant_firstname);
    await I.fillField('#primaryApplicantSurname', admonWillDetailsConfig.applicant_lastname);

    if (updateAddressManually) {
        await I.click(admonWillDetailsConfig.UKpostcodeLink);
    }

    await I.fillField('#primaryApplicantAddress__detailAddressLine1', admonWillDetailsConfig.address_line1);
    await I.fillField('#primaryApplicantAddress__detailAddressLine2', admonWillDetailsConfig.address_line2);
    await I.fillField('#primaryApplicantAddress__detailAddressLine3', admonWillDetailsConfig.address_line3);
    await I.fillField('#primaryApplicantAddress__detailPostTown', admonWillDetailsConfig.address_town);
    await I.fillField('#primaryApplicantAddress__detailCounty', admonWillDetailsConfig.address_county);
    await I.fillField('#primaryApplicantAddress__detailPostCode', admonWillDetailsConfig.address_postcode);
    await I.fillField('#primaryApplicantAddress__detailCountry', admonWillDetailsConfig.address_country);

    await I.fillField('#primaryApplicantPhoneNumber', admonWillDetailsConfig.applicant_phone);
    await I.fillField('#primaryApplicantEmailAddress', admonWillDetailsConfig.applicant_email);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
