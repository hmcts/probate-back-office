'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForClickable({css: `#dispenseWithNotice-${grantOfProbateConfig.optionYes}`});
    await I.runAccessibilityTest();
    await I.click(`#dispenseWithNotice-${grantOfProbateConfig.optionYes}`);
    await I.waitForClickable({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
