'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// deceased details part 2
module.exports = async function (caseProgressConfig) {
    const I = this;
    await I.waitForText(caseProgressConfig.IHT205Label);
    await I.waitForText(caseProgressConfig.IHT2207Label);
    await I.waitForText(caseProgressConfig.IHT400Label);
    await I.waitForText(caseProgressConfig.IHTDNULabel);
    await I.click({css: `#ihtFormId-${caseProgressConfig.IHTOption}`});
    await I.waitForElement({css: '#iht217_Yes'});
    await I.click({css: '#iht217_Yes'});
    await I.fillField({css: '#ihtGrossValue'}, caseProgressConfig.IHTGross);
    await I.fillField({css: '#ihtNetValue'}, caseProgressConfig.IHTNet);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
