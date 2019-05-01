'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createCaveatConfig.page3_waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#caveatorForenames', createCaveatConfig.page3_caveator_forenames);
        I.fillField('#caveatorSurname', createCaveatConfig.page3_caveator_surname);

        I.fillField('#caveatorEmailAddress', createCaveatConfig.page3_caveator_email);

        I.click(createCaveatConfig.UKpostcodeLink);
        I.fillField('#caveatorAddress__AddressLine1', createCaveatConfig.address_line1);
        I.fillField('#caveatorAddress__AddressLine2', createCaveatConfig.address_line2);
        I.fillField('#caveatorAddress__AddressLine3', createCaveatConfig.address_line3);
        I.fillField('#caveatorAddress__PostTown', createCaveatConfig.address_town);
        I.fillField('#caveatorAddress__County', createCaveatConfig.address_county);
        I.fillField('#caveatorAddress__PostCode', createCaveatConfig.address_postcode);
        I.fillField('#caveatorAddress__Country', createCaveatConfig.address_country);
    }

    if (crud === 'update') {
        I.waitForText(createCaveatConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#caveatorForenames', createCaveatConfig.page3_caveator_forenames_update);
        I.fillField('#caveatorSurname', createCaveatConfig.page3_caveator_surname_update);
    }

    if (crud === 'import') {
        I.waitForText(createCaveatConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
