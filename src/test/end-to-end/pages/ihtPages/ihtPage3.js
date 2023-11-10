'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// deceased details part 2
module.exports = async function (ihtGrossValue, ihtNetValue) {
    const I = this;
    await I.waitForElement({css: '#ihtGrossValue'});
    await I.fillField({css: '#ihtGrossValue'}, ihtGrossValue);
    await I.waitForElement({css: '#ihtNetValue'});
    await I.fillField({css: '#ihtNetValue'}, ihtNetValue);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
