'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseProgressConfig) {
    const I = this;
    await I.waitForElement('#solsSolicitorFirmName');
    await I.fillField('#solsSolicitorFirmName', caseProgressConfig.solFirmName);
    await I.click('#solsSolicitorIsExec_Yes');
    await I.fillField('#solsSOTForenames', caseProgressConfig.solFirstname);
    await I.fillField('#solsSOTSurname', caseProgressConfig.solSurname);
    await I.click(`#solsSolicitorIsMainApplicant_${caseProgressConfig.solIsMainApplicant ? 'Yes' : 'No'}`);
    if (!caseProgressConfig.solIsMainApplicant) {
        const locator = {css: `#solsSolicitorIsApplying_${caseProgressConfig.solIsApplying ? 'Yes' : 'No'}`};
        await I.waitForClickable(locator);
        await I.click(locator);
        if (!caseProgressConfig.solIsApplying) {
            await I.waitForVisible({css: '#solsSolicitorNotApplyingReason'});
            await I.selectOption({css: '#solsSolicitorNotApplyingReason'}, 'They lack capacity to act as executor');
        }
    }
    await I.click('#solsSolicitorAddress_solsSolicitorAddress a');
    await I.fillField('#solsSolicitorAddress__detailAddressLine1', caseProgressConfig.solAddr1);
    await I.fillField('#solsSolicitorAddress__detailPostTown', caseProgressConfig.solAddrTown);
    await I.fillField('#solsSolicitorAddress__detailCounty', caseProgressConfig.solAddrCounty);
    await I.fillField('#solsSolicitorAddress__detailPostCode', caseProgressConfig.solAddrPostcode);
    await I.fillField('#solsSolicitorAddress__detailCountry', caseProgressConfig.solAddrCountry);
    await I.fillField('#solsSolicitorAppReference', caseProgressConfig.ref);
    await I.fillField('#solsSolicitorEmail', caseProgressConfig.solEmail);
    await I.fillField('#solsSolicitorPhoneNumber', caseProgressConfig.solPhone);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
