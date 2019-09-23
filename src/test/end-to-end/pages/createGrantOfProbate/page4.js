'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud, unique_deceased_user) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createGrantOfProbateConfig.page4_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#boDeceasedTitle', createGrantOfProbateConfig.page4_bo_deceasedTitle);
        I.fillField('#deceasedForenames', createGrantOfProbateConfig.page4_deceasedForenames + '_' + unique_deceased_user);
        I.fillField('#deceasedSurname', createGrantOfProbateConfig.page4_deceasedSurname + '_' + unique_deceased_user);
        I.fillField('#boDeceasedHonours', createGrantOfProbateConfig.page4_bo_deceasedHonours);

        I.click(createGrantOfProbateConfig.UKpostcodeLink);
        I.fillField('#deceasedAddress_AddressLine1', createGrantOfProbateConfig.address_line1);
        I.fillField('#deceasedAddress_AddressLine2', createGrantOfProbateConfig.address_line2);
        I.fillField('#deceasedAddress_AddressLine3', createGrantOfProbateConfig.address_line3);
        I.fillField('#deceasedAddress_PostTown', createGrantOfProbateConfig.address_town);
        I.fillField('#deceasedAddress_County', createGrantOfProbateConfig.address_county);
        I.fillField('#deceasedAddress_PostCode', createGrantOfProbateConfig.address_postcode);
        I.fillField('#deceasedAddress_Country', createGrantOfProbateConfig.address_country);

        I.selectOption('#dateOfDeathType', createGrantOfProbateConfig.page4_dateOfDeathType);
        I.fillField('#deceasedDateOfBirth-day', createGrantOfProbateConfig.page4_deceasedDob_day);
        I.fillField('#deceasedDateOfBirth-month', createGrantOfProbateConfig.page4_deceasedDob_month);
        I.fillField('#deceasedDateOfBirth-year', createGrantOfProbateConfig.page4_deceasedDob_year);
        I.fillField('#deceasedDateOfDeath-day', createGrantOfProbateConfig.page4_deceasedDod_day);
        I.fillField('#deceasedDateOfDeath-month', createGrantOfProbateConfig.page4_deceasedDod_month);
        I.fillField('#deceasedDateOfDeath-year', createGrantOfProbateConfig.page4_deceasedDod_year);

        I.click(`#deceasedAnyOtherNames-${createGrantOfProbateConfig.page4_deceasedAnyOtherNamesYes}`);
        I.click('#solsDeceasedAliasNamesList > div > button');
        I.fillField('#solsDeceasedAliasNamesList_0_SolsAliasname', createGrantOfProbateConfig.page4_deceasedAlias + '_' + unique_deceased_user);
        I.selectOption('#deceasedMaritalStatus', createGrantOfProbateConfig.page4_deceasedMaritalStatus);

        I.click(`#foreignAsset-${createGrantOfProbateConfig.page4_foreignAssetYes}`);
        I.fillField('#foreignAssetEstateValue', createGrantOfProbateConfig.page4_foreignAssetEstateValue);

    }

    if (crud === 'update') {
        I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);

        I.selectOption('#selectionList', createGrantOfProbateConfig.page4_list1_update_option);
        I.waitForNavigationToComplete(commonConfig.continueButton);

        I.fillField('#deceasedForenames', createGrantOfProbateConfig.page4_deceasedForenames + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
        I.fillField('#deceasedSurname', createGrantOfProbateConfig.page4_deceasedSurname + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
        I.fillField('#solsDeceasedAliasNamesList_0_SolsAliasname', createGrantOfProbateConfig.page4_deceasedAlias + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);

        I.fillField('#deceasedDateOfDeath-day', createGrantOfProbateConfig.page4_deceasedDod_day_update);
        I.fillField('#deceasedDateOfDeath-month', createGrantOfProbateConfig.page4_deceasedDod_month_update);
        I.fillField('#deceasedDateOfDeath-year', createGrantOfProbateConfig.page4_deceasedDod_year_update);
        I.fillField('#deceasedDateOfBirth-day', createGrantOfProbateConfig.page4_deceasedDob_day_update);
        I.fillField('#deceasedDateOfBirth-month', createGrantOfProbateConfig.page4_deceasedDob_month_update);
        I.fillField('#deceasedDateOfBirth-year', createGrantOfProbateConfig.page4_deceasedDob_year_update);

        I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page9_ihtReferenceNumber_update);

    }

    if (crud === 'update2orig') {

        // "reverting" update back to defaults - to enable case-match with matching case
        I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);

        I.selectOption('#selectionList', createGrantOfProbateConfig.page4_list1_update_option);
        I.waitForNavigationToComplete(commonConfig.continueButton);

        I.fillField('#deceasedDateOfDeath-day', createGrantOfProbateConfig.page4_deceasedDod_day);
        I.fillField('#deceasedDateOfDeath-month', createGrantOfProbateConfig.page4_deceasedDod_month);
        I.fillField('#deceasedDateOfDeath-year', createGrantOfProbateConfig.page4_deceasedDod_year);
        I.fillField('#deceasedDateOfBirth-day', createGrantOfProbateConfig.page4_deceasedDob_day);
        I.fillField('#deceasedDateOfBirth-month', createGrantOfProbateConfig.page4_deceasedDob_month);
        I.fillField('#deceasedDateOfBirth-year', createGrantOfProbateConfig.page4_deceasedDob_year);

    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
