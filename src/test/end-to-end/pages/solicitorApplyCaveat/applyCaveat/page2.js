'use strict';

const applyCaveatConfig = require('./applyCaveat');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsSolicitorFirmName');
    await I.fillField('#solsSolicitorFirmName', applyCaveatConfig.page2_firm_name);

    await I.click(applyCaveatConfig.UKpostcodeLink);
    await I.fillField('#caveatorAddress_AddressLine1', applyCaveatConfig.address_line1);
    await I.fillField('#caveatorAddress_AddressLine2', applyCaveatConfig.address_line2);
    await I.fillField('#caveatorAddress_AddressLine3', applyCaveatConfig.address_line3);
    await I.fillField('#caveatorAddress_PostTown', applyCaveatConfig.address_town);
    await I.fillField('#caveatorAddress_County', applyCaveatConfig.address_county);
    await I.fillField('#caveatorAddress_PostCode', applyCaveatConfig.address_postcode);
    await I.fillField('#caveatorAddress_Country', applyCaveatConfig.address_country);

    await I.fillField('#solsSolicitorAppReference', applyCaveatConfig.page2_app_ref);
    await I.fillField('#caveatorEmailAddress', applyCaveatConfig.page2_caveator_email);
    await I.fillField('#solsSolicitorPhoneNumber', applyCaveatConfig.page2_phone_num);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
