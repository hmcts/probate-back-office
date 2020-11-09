const caseProgressConfig = require('./caseProgressConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// deceased details part 2
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('#applicationGrounds'); 
    await I.fillField('#applicationGrounds', caseProgressConfig.page5_groundsForApplication);
    await I.click('#deceasedDomicileInEngWales-Yes');
    await I.click('a.manual-link');
    await I.fillField('#deceasedAddress_AddressLine1', caseProgressConfig.page5_deceasedAddr1);
    await I.fillField('#deceasedAddress_PostTown', caseProgressConfig.page5_deceasedAddrTown);
    await I.fillField('#deceasedAddress_County', caseProgressConfig.page5_deceasedAddrCounty);
    await I.fillField('#deceasedAddress_PostCode', caseProgressConfig.page5_deceasedAddrPostcode);
    await I.fillField('#deceasedAddress_Country', caseProgressConfig.page5_deceasedAddrCountry);
    await I.click('#deceasedAnyOtherNames-No');
    await I.selectOption('select', caseProgressConfig.page5_IHTSelectValue);
    await I.fillField('#ihtGrossValue', caseProgressConfig.page5_IHTGross);
    await I.fillField('#ihtNetValue', caseProgressConfig.page5_IHTNet);
    await I.waitForNavigationToComplete(commonConfig.continueButton);    
};
