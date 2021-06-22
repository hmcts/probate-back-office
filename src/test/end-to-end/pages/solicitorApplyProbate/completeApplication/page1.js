'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (willType = 'WillLeft') {
    const I = this;
    await I.waitForElement('#solsSOTNeedToUpdate');
    await I.runAccessibilityTest();
    if (willType === 'WillLeftAnnexed') {
        await I.see(completeApplicationConfig.page1_AdmonWilllegalStmtLink);
    } else if (willType === 'NoWill') {
        await I.see(completeApplicationConfig.page1_NoWilllegalStmtLink);
    } else {
        await I.see(completeApplicationConfig.page1_legalStmtLink);
    }

    await I.click(`#solsSOTNeedToUpdate_${completeApplicationConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
