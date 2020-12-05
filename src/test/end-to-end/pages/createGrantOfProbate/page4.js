'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, unique_deceased_user) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page4_waitForText, testConfig.TestTimeToWaitForText);
        await I.waitForElement({css: '#boDeceasedTitle'});
        await I.fillField({css: '#boDeceasedTitle'}, createGrantOfProbateConfig.page4_bo_deceasedTitle);

        await I.fillField({css: '#deceasedForenames'}, createGrantOfProbateConfig.page4_deceasedForenames + '_' + unique_deceased_user);
        await I.fillField({css: '#deceasedSurname'}, createGrantOfProbateConfig.page4_deceasedSurname + '_' + unique_deceased_user);
        await I.fillField('#boDeceasedHonours', createGrantOfProbateConfig.page4_bo_deceasedHonours);

        const pcLocator = {css: createGrantOfProbateConfig.UKpostcodeLink};
        await I.waitForVisible(pcLocator);
        await I.click(pcLocator);

        await I.waitForVisible({css: '#deceasedAddress_AddressLine1'});
        await I.fillField('#deceasedAddress_AddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#deceasedAddress_AddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#deceasedAddress_AddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#deceasedAddress_PostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#deceasedAddress_County', createGrantOfProbateConfig.address_county);
        await I.fillField('#deceasedAddress_PostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#deceasedAddress_Country', createGrantOfProbateConfig.address_country);

        await I.selectOption({css: '#dateOfDeathType'}, createGrantOfProbateConfig.page4_dateOfDeathType);
        await I.fillField({css: '#deceasedDateOfBirth-day'}, createGrantOfProbateConfig.page4_deceasedDob_day);
        await I.fillField({css: '#deceasedDateOfBirth-month'}, createGrantOfProbateConfig.page4_deceasedDob_month);
        await I.fillField({css: '#deceasedDateOfBirth-year'}, createGrantOfProbateConfig.page4_deceasedDob_year);
        await I.fillField({css: '#deceasedDateOfDeath-day'}, createGrantOfProbateConfig.page4_deceasedDod_day);
        await I.fillField({css: '#deceasedDateOfDeath-month'}, createGrantOfProbateConfig.page4_deceasedDod_month);
        await I.fillField({css: '#deceasedDateOfDeath-year'}, createGrantOfProbateConfig.page4_deceasedDod_year);

        await I.click(`#deceasedAnyOtherNames-${createGrantOfProbateConfig.page4_deceasedAnyOtherNamesYes}`);
        await I.click('#solsDeceasedAliasNamesList > div > button');
        await I.waitForVisible('#solsDeceasedAliasNamesList_0_SolsAliasname');
        await I.fillField('#solsDeceasedAliasNamesList_0_SolsAliasname', createGrantOfProbateConfig.page4_deceasedAlias + '_' + unique_deceased_user);

        await I.selectOption({css: '#deceasedMaritalStatus'}, createGrantOfProbateConfig.page4_deceasedMaritalStatus);

        await I.click(`#foreignAsset-${createGrantOfProbateConfig.page4_foreignAssetYes}`);
        await I.waitForVisible('#foreignAssetEstateValue');
        await I.fillField('#foreignAssetEstateValue', createGrantOfProbateConfig.page4_foreignAssetEstateValue);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);

        await I.selectOption('#selectionList', createGrantOfProbateConfig.page4_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        await I.waitForVisible('#deceasedForenames');
        await I.fillField('#deceasedForenames', createGrantOfProbateConfig.page4_deceasedForenames + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
        await I.fillField('#deceasedSurname', createGrantOfProbateConfig.page4_deceasedSurname + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
        await I.fillField('#solsDeceasedAliasNamesList_0_SolsAliasname', createGrantOfProbateConfig.page4_deceasedAlias + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);

        await I.fillField('#deceasedDateOfDeath-day', createGrantOfProbateConfig.page4_deceasedDod_day_update);
        await I.fillField('#deceasedDateOfDeath-month', createGrantOfProbateConfig.page4_deceasedDod_month_update);
        await I.fillField('#deceasedDateOfDeath-year', createGrantOfProbateConfig.page4_deceasedDod_year_update);
        await I.fillField('#deceasedDateOfBirth-day', createGrantOfProbateConfig.page4_deceasedDob_day_update);
        await I.fillField('#deceasedDateOfBirth-month', createGrantOfProbateConfig.page4_deceasedDob_month_update);
        await I.fillField('#deceasedDateOfBirth-year', createGrantOfProbateConfig.page4_deceasedDob_year_update);

        await I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page9_ihtReferenceNumber_update);
    }

    if (crud === 'update2orig') {

        // "reverting" update back to defaults - to enable case-match with matching case
        await I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);

        await I.selectOption('#selectionList', createGrantOfProbateConfig.page4_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        await I.waitForVisible('#deceasedDateOfDeath-day');
        await I.fillField('#deceasedDateOfDeath-day', createGrantOfProbateConfig.page4_deceasedDod_day);
        await I.fillField('#deceasedDateOfDeath-month', createGrantOfProbateConfig.page4_deceasedDod_month);
        await I.fillField('#deceasedDateOfDeath-year', createGrantOfProbateConfig.page4_deceasedDod_year);
        await I.fillField('#deceasedDateOfBirth-day', createGrantOfProbateConfig.page4_deceasedDob_day);
        await I.fillField('#deceasedDateOfBirth-month', createGrantOfProbateConfig.page4_deceasedDob_month);
        await I.fillField('#deceasedDateOfBirth-year', createGrantOfProbateConfig.page4_deceasedDob_year);
    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
