'use strict';

const intestacyDetailsConfig = require('./intestacyDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#primaryApplicantForenames');
    await I.runAccessibilityTest();

    await I.fillField('#primaryApplicantForenames', intestacyDetailsConfig.applicant_firstname);
    await I.fillField('#primaryApplicantSurname', intestacyDetailsConfig.applicant_lastname);
    await I.click(intestacyDetailsConfig.UKpostcodeLink);

    await I.fillField('#primaryApplicantAddress__detailAddressLine1', intestacyDetailsConfig.address_line1);
    await I.fillField('#primaryApplicantAddress__detailAddressLine2', intestacyDetailsConfig.address_line2);
    await I.fillField('#primaryApplicantAddress__detailAddressLine3', intestacyDetailsConfig.address_line3);
    await I.fillField('#primaryApplicantAddress__detailPostTown', intestacyDetailsConfig.address_town);
    await I.fillField('#primaryApplicantAddress__detailCounty', intestacyDetailsConfig.address_county);
    await I.fillField('#primaryApplicantAddress__detailPostCode', intestacyDetailsConfig.address_postcode);
    await I.fillField('#primaryApplicantAddress__detailCountry', intestacyDetailsConfig.address_country);

    await I.fillField('#primaryApplicantPhoneNumber', intestacyDetailsConfig.applicant_phone);
    await I.fillField('#primaryApplicantEmailAddress', intestacyDetailsConfig.applicant_email);

    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
