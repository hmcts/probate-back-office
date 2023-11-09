'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// deceased details part 2
module.exports = async function (caseProgressConfig) {
    const I = this;
    await I.waitForElement({css: '#ihtGrossValue'});
    await I.fillField({css: '#ihtGrossValue'}, caseProgressConfig.IHTGross);
    await I.waitForElement({css: '#ihtNetValue'});
    await I.fillField({css: '#ihtNetValue'}, caseProgressConfig.IHTNet);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
