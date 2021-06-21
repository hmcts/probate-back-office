'use strict';

const deceasedDetailsConfig = require('./deceasedDetailsConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;

    await I.waitForElement('#deceasedDomicileInEngWales');
    await I.runAccessibilityTest();

    await I.click(`#deceasedDomicileInEngWales-${deceasedDetailsConfig.optionYes}`);
    await I.click(`#deceasedAnyOtherNames-${deceasedDetailsConfig.optionNo}`);

    await I.click(deceasedDetailsConfig.UKpostcodeLink);

    await I.fillField('#deceasedAddress_AddressLine1', deceasedDetailsConfig.address_line1);
    await I.fillField('#deceasedAddress_AddressLine2', deceasedDetailsConfig.address_line2);
    await I.fillField('#deceasedAddress_AddressLine3', deceasedDetailsConfig.address_line3);
    await I.fillField('#deceasedAddress_PostTown', deceasedDetailsConfig.address_town);
    await I.fillField('#deceasedAddress_County', deceasedDetailsConfig.address_county);
    await I.fillField('#deceasedAddress_PostCode', deceasedDetailsConfig.address_postcode);
    await I.fillField('#deceasedAddress_Country', deceasedDetailsConfig.address_country);

    await I.waitForText(deceasedDetailsConfig.page2_solsIHT205FormLabel);
    await I.waitForText(deceasedDetailsConfig.page2_solsIHT207FormLabel);
    await I.waitForText(deceasedDetailsConfig.page2_solsIHT400421FormLabel);
    await I.waitForText(deceasedDetailsConfig.page2_solsIHTDNUFormLabel);
    await I.click({css: `#ihtFormId-${deceasedDetailsConfig.page2_IHTOption}`});
    await I.fillField('#ihtGrossValue', deceasedDetailsConfig.page2_ihtGrossValue);
    await I.fillField('#ihtNetValue', deceasedDetailsConfig.page2_ihtNetValue);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
