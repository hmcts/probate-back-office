'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// deceased details part 2
module.exports = async function (caseProgressConfig) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('#applicationGrounds'); 
    await I.fillField('#applicationGrounds', caseProgressConfig.groundsForApplication);
    await I.click('#deceasedDomicileInEngWales-Yes');
    await I.click('a.manual-link');
    await I.fillField('#deceasedAddress_AddressLine1', caseProgressConfig.deceasedAddr1);
    await I.fillField('#deceasedAddress_PostTown', caseProgressConfig.deceasedAddrTown);
    await I.fillField('#deceasedAddress_County', caseProgressConfig.deceasedAddrCounty);
    await I.fillField('#deceasedAddress_PostCode', caseProgressConfig.deceasedAddrPostcode);
    await I.fillField('#deceasedAddress_Country', caseProgressConfig.deceasedAddrCountry);
    await I.click('#deceasedAnyOtherNames-No');
    await I.selectOption('select', caseProgressConfig.IHTSelectValue);
    await I.fillField('#ihtGrossValue', caseProgressConfig.IHTGross);
    await I.fillField('#ihtNetValue', caseProgressConfig.IHTNet);
    await I.waitForNavigationToComplete(commonConfig.continueButton);    
};
