'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(applyForGrantOfProbateConfig.page4_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#boDeceasedTitle', applyForGrantOfProbateConfig.page4_bo_deceasedTitle);
        I.fillField('#deceasedForenames', applyForGrantOfProbateConfig.page4_deceasedForenames);
        I.fillField('#deceasedSurname', applyForGrantOfProbateConfig.page4_deceasedSurname);
        I.fillField('#boDeceasedHonours', applyForGrantOfProbateConfig.page4_bo_deceasedHonours);

        I.click(applyForGrantOfProbateConfig.UKpostcodeLink);
        I.fillField('#deceasedAddress_AddressLine1', applyForGrantOfProbateConfig.address_line1);
        I.fillField('#deceasedAddress_AddressLine2', applyForGrantOfProbateConfig.address_line2);
        I.fillField('#deceasedAddress_AddressLine3', applyForGrantOfProbateConfig.address_line3);
        I.fillField('#deceasedAddress_PostTown', applyForGrantOfProbateConfig.address_town);
        I.fillField('#deceasedAddress_County', applyForGrantOfProbateConfig.address_county);
        I.fillField('#deceasedAddress_PostCode', applyForGrantOfProbateConfig.address_postcode);
        I.fillField('#deceasedAddress_Country', applyForGrantOfProbateConfig.address_country);

        I.fillField('#deceasedDateOfBirth-day', applyForGrantOfProbateConfig.page4_deceasedDob_day);
        I.fillField('#deceasedDateOfBirth-month', applyForGrantOfProbateConfig.page4_deceasedDob_month);
        I.fillField('#deceasedDateOfBirth-year', applyForGrantOfProbateConfig.page4_deceasedDob_year);
        I.fillField('#deceasedDateOfDeath-day', applyForGrantOfProbateConfig.page4_deceasedDod_day);
        I.fillField('#deceasedDateOfDeath-month', applyForGrantOfProbateConfig.page4_deceasedDod_month);
        I.fillField('#deceasedDateOfDeath-year', applyForGrantOfProbateConfig.page4_deceasedDod_year);

        I.click(`#deceasedAnyOtherNames-${applyForGrantOfProbateConfig.page4_deceasedAnyOtherNamesYes}`);
        I.click('#solsDeceasedAliasNamesList > div > button');
        I.fillField('#solsDeceasedAliasNamesList_0_SolsAliasname', applyForGrantOfProbateConfig.page4_deceasedAlias);
        I.selectOption('#deceasedMartialStatus', applyForGrantOfProbateConfig.page4_deceasedMartialStatus);

        I.click(`#foreignAsset-${applyForGrantOfProbateConfig.page4_foreignAssetYes}`);
        I.fillField('#foreignAssetEstateValue', applyForGrantOfProbateConfig.page4_foreignAssetEstateValue);

    }

    if (crud === 'update') {
        I.selectOption('#selectionList', applyForGrantOfProbateConfig.page4_list1_update_option);
        I.click(commonConfig.continueButton);

        I.fillField('#deceasedDateOfDeath-day', applyForGrantOfProbateConfig.page4_deceasedDod_day_update);
        I.fillField('#deceasedDateOfDeath-month', applyForGrantOfProbateConfig.page4_deceasedDod_month_update);
        I.fillField('#deceasedDateOfDeath-year', applyForGrantOfProbateConfig.page4_deceasedDod_year_update);

    }

    I.click(commonConfig.continueButton);
};
