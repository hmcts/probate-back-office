'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// deceased details
module.exports = async function (caseProgressConfig, uniqueSuffix) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('#deceasedForenames');
    await I.fillField('#deceasedForenames', caseProgressConfig.deceasedFirstname + uniqueSuffix);
    await I.fillField('#deceasedSurname', caseProgressConfig.deceasedSurname + uniqueSuffix);
    await I.fillField('#deceasedDateOfBirth-day', caseProgressConfig.deceasedDobDay);
    await I.fillField('#deceasedDateOfBirth-month', caseProgressConfig.deceasedDobMonth);
    await I.fillField('#deceasedDateOfBirth-year', caseProgressConfig.deceasedDobYear);
    await I.fillField('#deceasedDateOfDeath-day', caseProgressConfig.deathDay);
    await I.fillField('#deceasedDateOfDeath-month', caseProgressConfig.deathMonth);
    await I.fillField('#deceasedDateOfDeath-year', caseProgressConfig.deathYear);
    await I.waitForElement({css: '#deceasedDomicileInEngWales_Yes'});
    await I.click({css: '#deceasedDomicileInEngWales_Yes'});
    await I.click({css: 'a.manual-link'});
    await I.fillField({css: '#deceasedAddress__detailAddressLine1'}, caseProgressConfig.deceasedAddr1);
    await I.fillField({css: '#deceasedAddress__detailPostTown'}, caseProgressConfig.deceasedAddrTown);
    await I.fillField({css: '#deceasedAddress__detailCounty'}, caseProgressConfig.deceasedAddrCounty);
    await I.fillField({css: '#deceasedAddress__detailPostCode'}, caseProgressConfig.deceasedAddrPostcode);
    await I.fillField({css: '#deceasedAddress__detailCountry'}, caseProgressConfig.deceasedAddrCountry);
    await I.click({css: '#deceasedAnyOtherNames_No'});
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
