'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// deceased details part 2
module.exports = async function (caseProgressConfig) {
    const I = this;
    await I.waitForText(caseProgressConfig.IHT205Label);
    await I.waitForText(caseProgressConfig.IHT400Label);
    await I.click({css: `#ihtFormId-${caseProgressConfig.IHT400Option}`});
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
