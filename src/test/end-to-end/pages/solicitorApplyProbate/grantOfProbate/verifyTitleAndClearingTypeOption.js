'use strict';

const testConfig = require('src/test/config.cjs');

module.exports = async function (optName) {
    const I = this;
    const optLocator = {css: `#titleAndClearingType-${optName}`};
    await I.waitForElement(optLocator);
    await I.scrollTo(optLocator);
    await I.click(optLocator);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayLong);
    }
    const isNa = optName === 'TCTNoT';
    const isTrustOption = optName.startsWith('TCTTrustCorp');
    const allRenouncing = optName.endsWith('AllRenouncing');
    const isSuccessorFirm = optName === 'TCTPartSuccPowerRes' || optName === 'TCTSolePrinSucc' || optName === 'TCTPartSuccAllRenouncing' || optName === 'TCTPartSuccOthersRenouncing';

    if (!isNa && !allRenouncing && !isTrustOption && isSuccessorFirm) {
        await I.waitForText('Name of firm named in will');
        await I.scrollTo('#nameOfFirmNamedInWill');
    }
};
