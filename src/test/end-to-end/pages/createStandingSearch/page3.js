'use strict';

const testConfig = require('src/test/config');
const createStandingSearchConfig = require('./createStandingSearchConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createStandingSearchConfig.page3_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#applicantForenames', createStandingSearchConfig.page3_applicantForenames);
        I.fillField('#applicantSurname', createStandingSearchConfig.page3_applicantSurname);
        I.fillField('#applicantEmailAddress', createStandingSearchConfig.page3_email);

        I.click(createStandingSearchConfig.UKpostcodeLink);
        I.fillField('#applicantAddress_AddressLine1', createStandingSearchConfig.address_line1);
        I.fillField('#applicantAddress_AddressLine2', createStandingSearchConfig.address_line2);
        I.fillField('#applicantAddress_AddressLine3', createStandingSearchConfig.address_line3);
        I.fillField('#applicantAddress_PostTown', createStandingSearchConfig.address_town);
        I.fillField('#applicantAddress_County', createStandingSearchConfig.address_county);
        I.fillField('#applicantAddress_PostCode', createStandingSearchConfig.address_postcode);
        I.fillField('#applicantAddress_Country', createStandingSearchConfig.address_country);

    }

    if (crud === 'update') {
        I.waitForText(createStandingSearchConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#applicantForenames', createStandingSearchConfig.page3_applicantForenames_update);
        I.fillField('#applicantSurname', createStandingSearchConfig.page3_applicantSurname_update);

    }

    I.click(commonConfig.continueButton);
};
