'use strict';

module.exports = async function () {
    const I = this;
    const opts = ['TCTPartSuccPowerRes', 'TCTPartPowerRes', 'TCTSolePrinSucc', 'TCTSolePrin', 'TCTPartSuccAllRenouncing',
        'TCTPartAllRenouncing', 'TCTTrustCorpResWithSDJ', 'TCTTrustCorpResWithApp', 'TCTPartSuccOthersRenouncing', 'TCTPartOthersRenouncing', 'TCTNA'];
    for (let i = 0; i < opts.length; i++) {
        // eslint-disable-next-line no-await-in-loop
        await I.verifyTitleAndClearingTypeOption(opts[i]);
    }
};
