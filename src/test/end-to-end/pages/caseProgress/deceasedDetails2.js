'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// deceased details part 2
module.exports = async function (caseProgressConfig) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement({css: '#applicationGrounds'});
    await I.fillField({css: '#applicationGrounds'}, caseProgressConfig.groundsForApplication);
    await I.click({css: '#deceasedDomicileInEngWales-Yes'});
    await I.click({css: 'a.manual-link'});
    await I.fillField({css: '#deceasedAddress_AddressLine1'}, caseProgressConfig.deceasedAddr1);
    await I.fillField({css: '#deceasedAddress_PostTown'}, caseProgressConfig.deceasedAddrTown);
    await I.fillField({css: '#deceasedAddress_County'}, caseProgressConfig.deceasedAddrCounty);
    await I.fillField({css: '#deceasedAddress_PostCode'}, caseProgressConfig.deceasedAddrPostcode);
    await I.fillField({css: '#deceasedAddress_Country'}, caseProgressConfig.deceasedAddrCountry);
    await I.click({css: '#deceasedAnyOtherNames-No'});
    await I.click({css: `#ihtFormId-${caseProgressConfig.IHTOption}`});
    await I.fillField({css: '#ihtGrossValue'}, caseProgressConfig.IHTGross);
    await I.fillField({css: '#ihtNetValue'}, caseProgressConfig.IHTNet);
    await I.click({css: '#iht217-No'});
    
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
