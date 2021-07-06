'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (forXui) {
    const I = this;
    await I.waitForElement({css: '#next-step'});

    const penultimateOpt = await I.grabValueFrom({css: '#next-step option:nth-last-child(2)'});
    await I.selectOption({css: '#next-step'}, penultimateOpt);
    await I.waitForEnabled({css: commonConfig.goButton});

    await I.waitForNavigationToComplete(commonConfig.goButton, forXui);
};
