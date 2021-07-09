'use strict';

const assert = require('assert');
const testConfig = require('src/test/config.js');

module.exports = async function (optName) {
    const I = this;

    const optLocator = {css: `#titleAndClearingType-${optName}`};
    await I.waitForElement(optLocator);
    await I.scrollTo(optLocator);
    await I.click(optLocator);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort);
    }
    const isNa = optName === 'TCTNoT';
    const isTrustOption = optName.startsWith('TCTTrustCorp');
    const allRenouncing = optName.endsWith('AllRenouncing');
    const allPowerRes = optName.endsWith('PowerRes');
    const isSuccessorFirm = optName === 'TCTPartSuccPowerRes' || optName === 'TCTSolePrinSucc' || optName === 'TCTPartSuccAllRenouncing' || optName === 'TCTPartSuccOthersRenouncing';

    const nameOfFirmNamedInWillVisible = (await I.grabNumberOfVisibleElements ({css: '#nameOfFirmNamedInWill'})) > 0;
    const nameOfSucceededFirmVisible = (await I.grabNumberOfVisibleElements ({css: '#nameOfSucceededFirm'})) > 0;
    const morePartnersHoldingPowerReservedVisible = (await I.grabNumberOfVisibleElements ({css: '#morePartnersHoldingPowerReserved'})) > 0;
    const anyOtherPartnersApplyingVisible = (await I.grabNumberOfVisibleElements ({css: '#anyOtherApplyingPartners_Yes'})) > 0;
    const practitionersPosnInTrustVisible = (await I.grabNumberOfVisibleElements ({css: '#probatePractitionersPositionInTrust'})) > 0;

    assert(isNa || isTrustOption ? !nameOfFirmNamedInWillVisible : nameOfFirmNamedInWillVisible);
    assert(isNa || isTrustOption || !isSuccessorFirm ? !nameOfSucceededFirmVisible : nameOfSucceededFirmVisible);
    assert(allPowerRes ? morePartnersHoldingPowerReservedVisible : !morePartnersHoldingPowerReservedVisible);
    assert(isNa || isTrustOption || allRenouncing ? !anyOtherPartnersApplyingVisible : anyOtherPartnersApplyingVisible);
    assert(isTrustOption ? practitionersPosnInTrustVisible : !practitionersPosnInTrustVisible);

    if (!isNa && !allRenouncing && !isTrustOption && isSuccessorFirm) {
        await I.waitForText('Name of firm named in will');
        await I.scrollTo('#nameOfFirmNamedInWill');
    }
};
