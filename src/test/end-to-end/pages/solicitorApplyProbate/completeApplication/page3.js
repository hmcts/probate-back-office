'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;

    await I.waitForElement('#solsReviewSOTConfirmCheckbox1');
    await I.runAccessibilityTest();
    await I.scrollTo({css: '#solsReviewSOTConfirmCheckbox1-BelieveTrue'});
    await I.click({css: '#solsReviewSOTConfirmCheckbox1-BelieveTrue'});
    await I.click({css: '#solsReviewSOTConfirmCheckbox2-BelieveTrue'});

    await I.waitForNavigationToComplete(commonConfig.continueButton);

};
