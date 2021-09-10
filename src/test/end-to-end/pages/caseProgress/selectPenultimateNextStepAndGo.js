'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function () {
    const I = this;
    await I.waitForEnabled({css: '#next-step'});

    const penultimateOpt = await I.grabValueFrom({css: '#next-step option:nth-last-child(2)'});
    await I.selectOption({css: '#next-step'}, penultimateOpt);
    await I.waitForEnabled({css: commonConfig.submitButton});
    await I.wait(testConfig.CaseworkerGoButtonClickDelay);
    await I.waitForNavigationToComplete(commonConfig.submitButton);
};
