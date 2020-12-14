'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page2_waitForText, testConfig.TestTimeToWaitForText);
        await I.fillField('#primaryApplicantForenames', createGrantOfProbateConfig.page2_firstnames);
        await I.fillField('#primaryApplicantSurname', createGrantOfProbateConfig.page2_lastnames);

        await I.fillField('#primaryApplicantPhoneNumber', createGrantOfProbateConfig.page2_phoneNumber);
        await I.fillField('#primaryApplicantSecondPhoneNumber', createGrantOfProbateConfig.page2_secondPhoneNumber);
        await I.fillField('#primaryApplicantEmailAddress', createGrantOfProbateConfig.page2_email);

        await I.selectOption('#primaryApplicantRelationshipToDeceased', createGrantOfProbateConfig.page2_relationshipToDeceased);

        await I.click(`#primaryApplicantHasAlias-${createGrantOfProbateConfig.page2_hasAliasYes}`);
        const aliasLocator = {css: '#primaryApplicantAlias'};
        await I.waitForVisible(aliasLocator);
        await I.fillField(aliasLocator, createGrantOfProbateConfig.page2_alias);

        await I.click(`#primaryApplicantIsApplying-${createGrantOfProbateConfig.page2_applyingYes}`);

        const pcLocator = {css: createGrantOfProbateConfig.UKpostcodeLink};
        await I.waitForVisible(pcLocator);
        await I.click(pcLocator);
        await I.waitForVisible({css: '#primaryApplicantAddress_AddressLine1'});
        await I.fillField('#primaryApplicantAddress_AddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#primaryApplicantAddress_AddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#primaryApplicantAddress_AddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#primaryApplicantAddress_PostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#primaryApplicantAddress_County', createGrantOfProbateConfig.address_county);
        await I.fillField('#primaryApplicantAddress_PostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#primaryApplicantAddress_Country', createGrantOfProbateConfig.address_country);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page2_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page2_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.fillField('#primaryApplicantForenames', createGrantOfProbateConfig.page2_firstnames_update);
        await I.fillField('#primaryApplicantSurname', createGrantOfProbateConfig.page2_lastnames_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
