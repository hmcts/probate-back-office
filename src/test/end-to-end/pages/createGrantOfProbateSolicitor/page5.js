'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, unique_deceased_user) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page5_waitForText, testConfig.WaitForTextTimeout);
        await I.waitForElement({css: '#boDeceasedTitle'});
        await I.fillField({css: '#boDeceasedTitle'}, createGrantOfProbateConfig.page5_bo_deceasedTitle);

        await I.fillField({css: '#deceasedForenames'}, createGrantOfProbateConfig.page5_deceasedForenames + '_' + unique_deceased_user);
        await I.fillField({css: '#deceasedSurname'}, createGrantOfProbateConfig.page5_deceasedSurname + '_' + unique_deceased_user);
        await I.fillField('#boDeceasedHonours', createGrantOfProbateConfig.page5_bo_deceasedHonours);

        const pcLocator = {css: createGrantOfProbateConfig.UKpostcodeLink};
        await I.waitForVisible(pcLocator);
        await I.click(pcLocator);

        await I.waitForVisible({css: '#deceasedAddress__detailAddressLine1'});
        await I.fillField('#deceasedAddress__detailAddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#deceasedAddress__detailAddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#deceasedAddress__detailAddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#deceasedAddress__detailPostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#deceasedAddress__detailCounty', createGrantOfProbateConfig.address_county);
        await I.fillField('#deceasedAddress__detailPostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#deceasedAddress__detailCountry', createGrantOfProbateConfig.address_country);

        await I.selectOption({css: '#dateOfDeathType'}, createGrantOfProbateConfig.page5_dateOfDeathType);
        await I.fillField({css: '#deceasedDateOfBirth-day'}, createGrantOfProbateConfig.page5_deceasedDob_day);
        await I.fillField({css: '#deceasedDateOfBirth-month'}, createGrantOfProbateConfig.page5_deceasedDob_month);
        await I.fillField({css: '#deceasedDateOfBirth-year'}, createGrantOfProbateConfig.page5_deceasedDob_year);
        await I.fillField({css: '#deceasedDateOfDeath-day'}, createGrantOfProbateConfig.page5_deceasedDod_day);
        await I.fillField({css: '#deceasedDateOfDeath-month'}, createGrantOfProbateConfig.page5_deceasedDod_month);
        await I.fillField({css: '#deceasedDateOfDeath-year'}, createGrantOfProbateConfig.page5_deceasedDod_year);

        await I.click(`#deceasedAnyOtherNames_${createGrantOfProbateConfig.page5_deceasedAnyOtherNamesYes}`);
        await I.click('#solsDeceasedAliasNamesList > div > button');
        await I.waitForVisible('#solsDeceasedAliasNamesList_0_SolsAliasname');
        await I.fillField('#solsDeceasedAliasNamesList_0_SolsAliasname', createGrantOfProbateConfig.page5_deceasedAlias + '_' + unique_deceased_user);

        await I.click(`#deceasedMaritalStatus-${createGrantOfProbateConfig.page5_deceasedMaritalStatus}`);
        await I.click(`#foreignAsset_${createGrantOfProbateConfig.page5_foreignAssetYes}`);
        await I.waitForVisible('#foreignAssetEstateValue');
        await I.fillField('#foreignAssetEstateValue', createGrantOfProbateConfig.page5_foreignAssetEstateValue);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page5_amend_waitForText, testConfig.WaitForTextTimeout);

        await I.selectOption('#selectionList', createGrantOfProbateConfig.page5_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        await I.waitForVisible('#deceasedForenames');
        await I.fillField('#deceasedForenames', createGrantOfProbateConfig.page5_deceasedForenames + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
        await I.fillField('#deceasedSurname', createGrantOfProbateConfig.page5_deceasedSurname + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
        await I.fillField('#solsDeceasedAliasNamesList_0_SolsAliasname', createGrantOfProbateConfig.page5_deceasedAlias + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);

        await I.fillField('#deceasedDateOfDeath-day', createGrantOfProbateConfig.page5_deceasedDod_day_update);
        await I.fillField('#deceasedDateOfDeath-month', createGrantOfProbateConfig.page5_deceasedDod_month_update);
        await I.fillField('#deceasedDateOfDeath-year', createGrantOfProbateConfig.page5_deceasedDod_year_update);
        await I.fillField('#deceasedDateOfBirth-day', createGrantOfProbateConfig.page5_deceasedDob_day_update);
        await I.fillField('#deceasedDateOfBirth-month', createGrantOfProbateConfig.page5_deceasedDob_month_update);
        await I.fillField('#deceasedDateOfBirth-year', createGrantOfProbateConfig.page5_deceasedDob_year_update);

        await I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page10_ihtReferenceNumber_update);
    }

    if (crud === 'update2orig') {

        // "reverting" update back to defaults - to enable case-match with matching case
        await I.waitForText(createGrantOfProbateConfig.page5_amend_waitForText, testConfig.WaitForTextTimeout);

        await I.selectOption('#selectionList', createGrantOfProbateConfig.page5_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        await I.waitForVisible('#deceasedDateOfDeath-day');
        await I.fillField('#deceasedDateOfDeath-day', createGrantOfProbateConfig.page5_deceasedDod_day);
        await I.fillField('#deceasedDateOfDeath-month', createGrantOfProbateConfig.page5_deceasedDod_month);
        await I.fillField('#deceasedDateOfDeath-year', createGrantOfProbateConfig.page5_deceasedDod_year);
        await I.fillField('#deceasedDateOfBirth-day', createGrantOfProbateConfig.page5_deceasedDob_day);
        await I.fillField('#deceasedDateOfBirth-month', createGrantOfProbateConfig.page5_deceasedDob_month);
        await I.fillField('#deceasedDateOfBirth-year', createGrantOfProbateConfig.page5_deceasedDob_year);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
