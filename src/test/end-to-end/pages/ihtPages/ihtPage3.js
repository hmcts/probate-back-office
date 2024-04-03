'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// deceased details part 2
module.exports = async function (ihtGrossValue, ihtNetValue, whichIHTFormsCompleted) {
    const I = this;
    await I.waitForElement({css: '#ihtGrossValue'});
    await I.fillField({css: '#ihtGrossValue'}, ihtGrossValue);
    if (whichIHTFormsCompleted === 'IHT400') {
        await I.waitForElement({css: '#ihtFormNetValue'});
        await I.fillField({css: '#ihtFormNetValue'}, ihtNetValue);
    } else {
        await I.waitForElement({css: '#ihtNetValue'});
        await I.fillField({css: '#ihtNetValue'}, ihtNetValue);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
