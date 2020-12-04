'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createCaveatConfig.page3_waitForText, testConfig.TestTimeToWaitForText);

        await I.fillField('#caveatorForenames', createCaveatConfig.page3_caveator_forenames);
        await I.fillField('#caveatorSurname', createCaveatConfig.page3_caveator_surname);

        await I.fillField('#caveatorEmailAddress', createCaveatConfig.page3_caveator_email);
        await I.fillField('#solsSolicitorAppReference', createCaveatConfig.page3_solAppReference);

        await I.click(createCaveatConfig.UKpostcodeLink);
        await I.fillField('#caveatorAddress_AddressLine1', createCaveatConfig.address_line1);
        await I.fillField('#caveatorAddress_AddressLine2', createCaveatConfig.address_line2);
        await I.fillField('#caveatorAddress_AddressLine3', createCaveatConfig.address_line3);
        await I.fillField('#caveatorAddress_PostTown', createCaveatConfig.address_town);
        await I.fillField('#caveatorAddress_County', createCaveatConfig.address_county);
        await I.fillField('#caveatorAddress_PostCode', createCaveatConfig.address_postcode);
        await I.fillField('#caveatorAddress_Country', createCaveatConfig.address_country);
        await I.click(`#languagePreferenceWelsh-${createCaveatConfig.page3_langPrefNo}`);
    }

    if (crud === 'update') {
        await I.waitForText(createCaveatConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);

        await I.fillField('#caveatorForenames', createCaveatConfig.page3_caveator_forenames_update);
        await I.fillField('#caveatorSurname', createCaveatConfig.page3_caveator_surname_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
