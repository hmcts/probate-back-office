'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// deceased details part 2
module.exports = async function (caseProgressConfig) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement({css: '#deceasedDomicileInEngWales_Yes'});
    await I.click({css: '#deceasedDomicileInEngWales_Yes'});
    await I.click({css: 'a.manual-link'});
    await I.fillField({css: '#deceasedAddress__detailAddressLine1'}, caseProgressConfig.deceasedAddr1);
    await I.fillField({css: '#deceasedAddress__detailPostTown'}, caseProgressConfig.deceasedAddrTown);
    await I.fillField({css: '#deceasedAddress__detailCounty'}, caseProgressConfig.deceasedAddrCounty);
    await I.fillField({css: '#deceasedAddress__detailPostCode'}, caseProgressConfig.deceasedAddrPostcode);
    await I.fillField({css: '#deceasedAddress__detailCountry'}, caseProgressConfig.deceasedAddrCountry);
    await I.click({css: '#deceasedAnyOtherNames_No'});
    await I.waitForText(caseProgressConfig.IHT205Label);
    await I.waitForText(caseProgressConfig.IHT2207Label);
    await I.waitForText(caseProgressConfig.IHT400Label);
    await I.waitForText(caseProgressConfig.IHTDNULabel);
    await I.click({css: `#ihtFormId-${caseProgressConfig.IHTOption}`});
    await I.fillField({css: '#ihtGrossValue'}, caseProgressConfig.IHTGross);
    await I.fillField({css: '#ihtNetValue'}, caseProgressConfig.IHTNet);
    await I.click({css: '#iht217_No'});

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
