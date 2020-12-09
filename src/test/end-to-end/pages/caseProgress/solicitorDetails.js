'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseProgressConfig) {
    const I = this;
    await I.waitForElement('#solsSolicitorFirmName');
    await I.fillField('#solsSolicitorFirmName', caseProgressConfig.solFirmName);
    await I.click('#solsSolicitorIsExec-Yes');
    await I.fillField('#solsSOTForenames', caseProgressConfig.solFirstname);
    await I.fillField('#solsSOTSurname', caseProgressConfig.solSurname);
    await I.click('#solsSolicitorIsMainApplicant-Yes');
    await I.click('#solsSolicitorAddress_solsSolicitorAddress a');
    await I.fillField('#solsSolicitorAddress_AddressLine1', caseProgressConfig.solAddr1);
    await I.fillField('#solsSolicitorAddress_PostTown', caseProgressConfig.solAddrTown);
    await I.fillField('#solsSolicitorAddress_County', caseProgressConfig.solAddrCounty);
    await I.fillField('#solsSolicitorAddress_PostCode', caseProgressConfig.solAddrPostcode);
    await I.fillField('#solsSolicitorAddress_Country', caseProgressConfig.solAddrCountry);
    await I.fillField('#solsSolicitorAppReference', caseProgressConfig.ref);
    await I.fillField('#solsSolicitorEmail', caseProgressConfig.solEmail);
    await I.fillField('#solsSolicitorPhoneNumber', caseProgressConfig.solPhone);
    await I.waitForNavigationToComplete(commonConfig.continueButton);    
};
