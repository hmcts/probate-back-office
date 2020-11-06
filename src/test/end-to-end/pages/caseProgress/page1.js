'use strict';

const testConfig = require('src/test/config');
const caseProgressConfig = require('./caseProgressConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.fillField('#solsSolicitorFirmName', caseProgressConfig.page1_solFirmName);
    await I.click('#solsSolicitorIsExec-Yes');
    await I.fillField('#solsSOTForenames', caseProgressConfig.page1_solFirstname);
    await I.fillField('#solsSOTSurname', caseProgressConfig.page1_solSurname);
    await I.click('#solsSolicitorIsMainApplicant-Yes');
    await I.click('#solsSolicitorAddress_solsSolicitorAddress a');
    await I.fillField('#solsSolicitorAddress_AddressLine1', caseProgressConfig.page1_solFirmName);
    await I.fillField('#solsSolicitorAddress_PostTown', caseProgressConfig.page1_solAddrTown);
    await I.fillField('#solsSolicitorAddress_County', caseProgressConfig.page1_solAddrCounty);
    await I.fillField('#solsSolicitorAddress_PostCode', caseProgressConfig.page1_solAddrPostcode);
    await I.fillField('#solsSolicitorAddress_Country', caseProgressConfig.page1_solAddrCountry);
    await I.fillField('#solsSolicitorAppReference', caseProgressConfig.page1_ref);
    await I.fillField('#solsSolicitorEmail', caseProgressConfig.page1_solEmail);
    await I.fillField('#solsSolicitorPhoneNumber', caseProgressConfig.page1_solPhone);
    await I.waitForNavigationToComplete(commonConfig.continueButton);    
};
