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
    const isNa = optName === 'TCTNA';
    const isTrustOption = optName.startsWith('TCTTrustCorp');

    const isSuccessorFirm = optName === 'TCTPartSuccPowerRes' || optName === 'TCTSolePrinSucc' || optName === 'TCTPartSuccAllRenouncing';

    const nameOfFirmNamedInWillVisible = (await I.grabNumberOfVisibleElements ({css: '#nameOfFirmNamedInWill'})) > 0;
    const otherPartnerExecutorNameVisible = (await I.grabNumberOfVisibleElements ({css: '#otherPartnerExecutorName'})) > 0;
    const anyPartnersApplyingToActAsExecutorYesVisible = (await I.grabNumberOfVisibleElements ({css: '#anyPartnersApplyingToActAsExecutor-Yes'})) > 0;
    const nameOfSucceededFirmVisible = (await I.grabNumberOfVisibleElements ({css: '#nameOfSucceededFirm'})) > 0;

    assert (isNa || isTrustOption ? !nameOfFirmNamedInWillVisible : nameOfFirmNamedInWillVisible);
    assert (isNa || isTrustOption ? !otherPartnerExecutorNameVisible : otherPartnerExecutorNameVisible);
    assert (isNa || isTrustOption ? !anyPartnersApplyingToActAsExecutorYesVisible : anyPartnersApplyingToActAsExecutorYesVisible);
    assert (isNa || isTrustOption || !isSuccessorFirm ? !nameOfSucceededFirmVisible : nameOfSucceededFirmVisible);

    if (!isNa && !isTrustOption && isSuccessorFirm) {
        await I.click({css: '#anyPartnersApplyingToActAsExecutor-Yes'});
        await I.waitForText('Names of executors applying');
        await I.waitForClickable({css: '#otherPartnersApplyingAsExecutors button'});
        await I.click({css: '#anyPartnersApplyingToActAsExecutor-No'});
        await I.waitForInvisible({css: '#otherPartnersApplyingAsExecutors button'});
    }
};
