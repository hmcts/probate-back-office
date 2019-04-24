'use strict';

const testConfig = require('src/test/config');
const createStandingSearchConfig = require('./createStandingSearchConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud, sol=false) {

    const I = this;

    if (crud === 'create') {
        if (sol) {
            I.waitForText(createStandingSearchConfig.page3_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_solForenames);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_solSurname);
            I.fillField('#applicantEmailAddress', createStandingSearchConfig.page3_email);

            I.click(createStandingSearchConfig.UKpostcodeLink);
            I.fillField('#applicantAddress__AddressLine1', createStandingSearchConfig.sol_address_line1);
            I.fillField('#applicantAddress__AddressLine2', createStandingSearchConfig.sol_address_line2);
            I.fillField('#applicantAddress__AddressLine3', createStandingSearchConfig.sol_address_line3);
            I.fillField('#applicantAddress__PostTown', createStandingSearchConfig.sol_address_town);
            I.fillField('#applicantAddress__County', createStandingSearchConfig.sol_address_county);
            I.fillField('#applicantAddress__PostCode', createStandingSearchConfig.sol_address_postcode);
            I.fillField('#applicantAddress__Country', createStandingSearchConfig.sol_address_country);
        } else {
            I.waitForText(createStandingSearchConfig.page3_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_applicantForenames);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_applicantSurname);
            I.fillField('#applicantEmailAddress', createStandingSearchConfig.page3_email);

            I.click(createStandingSearchConfig.UKpostcodeLink);
            I.fillField('#applicantAddress__AddressLine1', createStandingSearchConfig.address_line1);
            I.fillField('#applicantAddress__AddressLine2', createStandingSearchConfig.address_line2);
            I.fillField('#applicantAddress__AddressLine3', createStandingSearchConfig.address_line3);
            I.fillField('#applicantAddress__PostTown', createStandingSearchConfig.address_town);
            I.fillField('#applicantAddress__County', createStandingSearchConfig.address_county);
            I.fillField('#applicantAddress__PostCode', createStandingSearchConfig.address_postcode);
            I.fillField('#applicantAddress__Country', createStandingSearchConfig.address_country);
        }
    }

    if (crud === 'update1') {
        if (sol) {
            createStandingSearchConfig.page3_solForenames_update = `${createStandingSearchConfig.page3_solForenames}_${crud}`;
            createStandingSearchConfig.page3_solSurname_update = `${createStandingSearchConfig.page3_solSurname}_${crud}`;

            I.waitForText(createStandingSearchConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_solForenames_update);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_solSurname_update);
        } else {
            createStandingSearchConfig.page3_applicantForenames_update = `${createStandingSearchConfig.page3_applicantForenames}_${crud}`;
            createStandingSearchConfig.page3_applicantSurname_update = `${createStandingSearchConfig.page3_applicantSurname}_${crud}`;

            I.waitForText(createStandingSearchConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_applicantForenames_update);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_applicantSurname_update);
        }
    }

    if (crud === 'update2') {
        if (sol) {
            createStandingSearchConfig.page3_solForenames_update = `${createStandingSearchConfig.page3_solForenames}_${crud}`;
            createStandingSearchConfig.page3_solSurname_update = `${createStandingSearchConfig.page3_solSurname}_${crud}`;

            I.waitForText(createStandingSearchConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_solForenames_update);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_solSurname_update);
        } else {
            createStandingSearchConfig.page3_applicantForenames_update = `${createStandingSearchConfig.page3_applicantForenames}_${crud}`;
            createStandingSearchConfig.page3_applicantSurname_update = `${createStandingSearchConfig.page3_applicantSurname}_${crud}`;

            I.waitForText(createStandingSearchConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_applicantForenames_update);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_applicantSurname_update);
        }
    }

    if (crud === 'update3') {
        if (sol) {
            createStandingSearchConfig.page3_solForenames_update = `${createStandingSearchConfig.page3_solForenames}_${crud}`;
            createStandingSearchConfig.page3_solSurname_update = `${createStandingSearchConfig.page3_solSurname}_${crud}`;

            I.waitForText(createStandingSearchConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_solForenames_update);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_solSurname_update);
        } else {
            createStandingSearchConfig.page3_applicantForenames_update = `${createStandingSearchConfig.page3_applicantForenames}_${crud}`;
            createStandingSearchConfig.page3_applicantSurname_update = `${createStandingSearchConfig.page3_applicantSurname}_${crud}`;

            I.waitForText(createStandingSearchConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_applicantForenames_update);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_applicantSurname_update);
        }
    }

    if (crud === 'update4') {
        if (sol) {
            createStandingSearchConfig.page3_solForenames_update = `${createStandingSearchConfig.page3_solForenames}_${crud}`;
            createStandingSearchConfig.page3_solSurname_update = `${createStandingSearchConfig.page3_solSurname}_${crud}`;
    
            I.waitForText(createStandingSearchConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_solForenames_update);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_solSurname_update);
        } else {
            createStandingSearchConfig.page3_applicantForenames_update = `${createStandingSearchConfig.page3_applicantForenames}_${crud}`;
            createStandingSearchConfig.page3_applicantSurname_update = `${createStandingSearchConfig.page3_applicantSurname}_${crud}`;
    
            I.waitForText(createStandingSearchConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
            I.fillField('#applicantForenames', createStandingSearchConfig.page3_applicantForenames_update);
            I.fillField('#applicantSurname', createStandingSearchConfig.page3_applicantSurname_update);
        }
    }

    I.click(commonConfig.continueButton);
};
