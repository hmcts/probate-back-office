'use strict';

const applyCaveatConfig = require('./applyCaveat');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsSolicitorFirmName');
    await I.runAccessibilityTest();

    await I.fillField('#solsSolicitorFirmName', applyCaveatConfig.page2_firm_name);
    await I.click(applyCaveatConfig.UKpostcodeLink);
    await I.fillField('#caveatorAddress__detailAddressLine1', applyCaveatConfig.address_line1);
    await I.fillField('#caveatorAddress__detailAddressLine2', applyCaveatConfig.address_line2);
    await I.fillField('#caveatorAddress__detailAddressLine3', applyCaveatConfig.address_line3);
    await I.fillField('#caveatorAddress__detailPostTown', applyCaveatConfig.address_town);
    await I.fillField('#caveatorAddress__detailCounty', applyCaveatConfig.address_county);
    await I.fillField('#caveatorAddress__detailPostCode', applyCaveatConfig.address_postcode);
    await I.fillField('#caveatorAddress__detailCountry', applyCaveatConfig.address_country);

    await I.fillField('#solsSolicitorAppReference', applyCaveatConfig.page2_app_ref);
    await I.fillField('#caveatorEmailAddress', applyCaveatConfig.page2_caveator_email);
    await I.fillField('#solsSolicitorPhoneNumber', applyCaveatConfig.page2_phone_num);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
