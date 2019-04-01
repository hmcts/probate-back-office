'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createGrantOfProbateConfig.page2_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#primaryApplicantForenames', createGrantOfProbateConfig.page2_firstnames);
        I.fillField('#primaryApplicantSurname', createGrantOfProbateConfig.page2_lastnames);

        I.fillField('#primaryApplicantPhoneNumber', createGrantOfProbateConfig.page2_phoneNumber);
        I.fillField('#primaryApplicantSecondPhoneNumber', createGrantOfProbateConfig.page2_secondPhoneNumber);
        I.fillField('#primaryApplicantEmailAddress', createGrantOfProbateConfig.page2_email);

        I.click(createGrantOfProbateConfig.UKpostcodeLink);
        I.fillField('#primaryApplicantAddress_AddressLine1', createGrantOfProbateConfig.address_line1);
        I.fillField('#primaryApplicantAddress_AddressLine2', createGrantOfProbateConfig.address_line2);
        I.fillField('#primaryApplicantAddress_AddressLine3', createGrantOfProbateConfig.address_line3);
        I.fillField('#primaryApplicantAddress_PostTown', createGrantOfProbateConfig.address_town);
        I.fillField('#primaryApplicantAddress_County', createGrantOfProbateConfig.address_county);
        I.fillField('#primaryApplicantAddress_PostCode', createGrantOfProbateConfig.address_postcode);
        I.fillField('#primaryApplicantAddress_Country', createGrantOfProbateConfig.address_country);

        I.selectOption('#primaryApplicantRelationshipToDeceased', createGrantOfProbateConfig.page2_relationshipToDeceased);

        I.click(`#primaryApplicantHasAlias-${createGrantOfProbateConfig.page2_hasAliasYes}`);
        I.fillField('#primaryApplicantAlias', createGrantOfProbateConfig.page2_alias);

        I.click(`#primaryApplicantIsApplying-${createGrantOfProbateConfig.page2_applyingYes}`);

    }

    if (crud === 'update') {
        I.waitForText(createGrantOfProbateConfig.page2_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#selectionList', createGrantOfProbateConfig.page3_list1_update_option);
        I.click(commonConfig.continueButton);
        I.fillField('#primaryApplicantForenames', createGrantOfProbateConfig.page2_firstnames_update);
        I.fillField('#primaryApplicantSurname', createGrantOfProbateConfig.page2_lastnames_update);

    }

    I.click(commonConfig.continueButton);
};
