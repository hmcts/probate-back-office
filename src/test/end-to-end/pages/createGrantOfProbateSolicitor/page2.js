'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (createGrantOfProbateConfig.page1_solsSolicitorIsExec !== 'No' || createGrantOfProbateConfig.page1_solsSolicitorIsApplying !== 'No') {
        return;
    }

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page2_waitForText, testConfig.WaitForTextTimeout);
        await I.fillField('#primaryApplicantForenames', createGrantOfProbateConfig.page2_firstnames);
        await I.fillField('#primaryApplicantSurname', createGrantOfProbateConfig.page2_lastnames);

        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }

        await I.fillField('#primaryApplicantPhoneNumber', createGrantOfProbateConfig.page2_phoneNumber);
        await I.fillField('#primaryApplicantSecondPhoneNumber', createGrantOfProbateConfig.page2_secondPhoneNumber);
        await I.fillField('#primaryApplicantEmailAddress', createGrantOfProbateConfig.page2_email);

        await I.selectOption('#primaryApplicantRelationshipToDeceased', createGrantOfProbateConfig.page2_relationshipToDeceased);

        await I.click(`#primaryApplicantHasAlias_${createGrantOfProbateConfig.page2_hasAliasYes}`);

        const aliasLocator = {css: '#primaryApplicantAlias'};
        await I.waitForVisible(aliasLocator);
        await I.fillField(aliasLocator, createGrantOfProbateConfig.page2_alias);

        await I.click(`#primaryApplicantIsApplying_${createGrantOfProbateConfig.page2_applyingYes}`);

        const pcLocator = {css: createGrantOfProbateConfig.UKpostcodeLink};
        await I.waitForVisible(pcLocator);
        await I.click(pcLocator);
        await I.waitForVisible({css: '#primaryApplicantAddress__detailAddressLine1'});
        await I.fillField('#primaryApplicantAddress__detailAddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#primaryApplicantAddress__detailAddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#primaryApplicantAddress__detailAddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#primaryApplicantAddress__detailPostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#primaryApplicantAddress__detailCounty', createGrantOfProbateConfig.address_county);
        await I.fillField('#primaryApplicantAddress__detailPostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#primaryApplicantAddress__detailCountry', createGrantOfProbateConfig.address_country);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page2_amend_waitForText, testConfig.WaitForTextTimeout);
        await I.waitForEnabled({css: '#selectionList'});
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page2_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.fillField('#primaryApplicantForenames', createGrantOfProbateConfig.page2_firstnames_update);
        await I.fillField('#primaryApplicantSurname', createGrantOfProbateConfig.page2_lastnames_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
