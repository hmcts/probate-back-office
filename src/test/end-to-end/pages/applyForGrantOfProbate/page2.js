'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(applyForGrantOfProbateConfig.page2_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#primaryApplicantForenames', applyForGrantOfProbateConfig.page2_firstnames);
        I.fillField('#primaryApplicantSurname', applyForGrantOfProbateConfig.page2_lastnames);

        I.fillField('#primaryApplicantPhoneNumber', applyForGrantOfProbateConfig.page2_phoneNumber);
        I.fillField('#primaryApplicantSecondPhoneNumber', applyForGrantOfProbateConfig.page2_secondPhoneNumber);
        I.fillField('#primaryApplicantEmailAddress', applyForGrantOfProbateConfig.page2_email);

        I.click(applyForGrantOfProbateConfig.UKpostcodeLink);
        I.fillField('#primaryApplicantAddress_AddressLine1', applyForGrantOfProbateConfig.address_line1);
        I.fillField('#primaryApplicantAddress_AddressLine2', applyForGrantOfProbateConfig.address_line2);
        I.fillField('#primaryApplicantAddress_AddressLine3', applyForGrantOfProbateConfig.address_line3);
        I.fillField('#primaryApplicantAddress_PostTown', applyForGrantOfProbateConfig.address_town);
        I.fillField('#primaryApplicantAddress_County', applyForGrantOfProbateConfig.address_county);
        I.fillField('#primaryApplicantAddress_PostCode', applyForGrantOfProbateConfig.address_postcode);
        I.fillField('#primaryApplicantAddress_Country', applyForGrantOfProbateConfig.address_country);

        I.selectOption('#primaryApplicantRelationshipToDeceased', applyForGrantOfProbateConfig.page2_relationshipToDeceased);

        I.click(`#primaryApplicantHasAlias-${applyForGrantOfProbateConfig.page2_hasAliasYes}`);
        I.fillField('#primaryApplicantAlias', applyForGrantOfProbateConfig.page2_alias);

        I.click(`#primaryApplicantIsApplying-${applyForGrantOfProbateConfig.page2_applyingYes}`);

    }

    if (crud === 'update') {
        I.selectOption('#selectionList', applyForGrantOfProbateConfig.page3_list1_update_option);
        I.click(commonConfig.continueButton);
        I.fillField('#primaryApplicantForenames', applyForGrantOfProbateConfig.page2_firstnames_update);
        I.fillField('#primaryApplicantSurname', applyForGrantOfProbateConfig.page2_lastnames_update);

    }

    I.click(commonConfig.continueButton);
};
