'use strict';

const assert = require('assert');
const testConfig = require('src/test/config.js');

module.exports = async function (optName) {
    const I = this;

    const optLocator = {css: `#titleAndClearingType-${optName}`};
    await I.waitForElement(optLocator);
    await I.click(optLocator);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.25);
    }
    const isNa = optName === 'TCTNoT';
    const isTrustOption = optName.startsWith('TCTTrustCorp');

    const isSuccessorFirm = optName === 'TCTPartSuccPowerRes' || optName === 'TCTSolePrinSucc' || optName === 'TCTPartSuccAllRenouncing';

    const nameOfFirmNamedInWillVisible = (await I.grabNumberOfVisibleElements ({css: '#nameOfFirmNamedInWill'})) > 0;
    const nameOfSucceededFirmVisible = (await I.grabNumberOfVisibleElements ({css: '#nameOfSucceededFirm'})) > 0;

    assert (isNa || isTrustOption ? !nameOfFirmNamedInWillVisible : nameOfFirmNamedInWillVisible);
    assert (isNa || isTrustOption || !isSuccessorFirm ? !nameOfSucceededFirmVisible : nameOfSucceededFirmVisible);

    if (!isNa && !isTrustOption && isSuccessorFirm) {
        await I.waitForText('Name of firm named in will');
        await I.waitForClickable({css: '#otherPartnersApplyingAsExecutors button'});
    }
};
