'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.cjs');

module.exports = async function () {
    const I = this;
    await I.waitForEnabled({css: '#next-step'});

    const penultimateOpt = await I.grabTextFrom({css: '#next-step option:nth-last-child(2)'});
    if (penultimateOpt === 'Delete') {
        const penultimateOptNew = await I.grabValueFrom({css: '#next-step option:nth-child(3)'});
        await I.selectOption({css: '#next-step'}, penultimateOptNew);
    } else {
        const penultimateOptFinal = await I.grabValueFrom({css: '#next-step option:nth-last-child(2)'});
        await I.selectOption({css: '#next-step'}, penultimateOptFinal);
    }
    await I.waitForEnabled({css: commonConfig.submitButton});
    await I.wait(testConfig.CaseworkerGoButtonClickDelay);
    await I.waitForNavigationToComplete(commonConfig.submitButton);
};
