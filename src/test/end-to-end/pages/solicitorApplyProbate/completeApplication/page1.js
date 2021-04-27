'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (willType = 'WillLeft') {
    const I = this;
    await I.waitForElement('#solsSOTNeedToUpdate');
    await I.runAccessibilityTest();
    if (willType === 'WillLeftAnnexed') {
        await I.waitForVisible('#solsReviewLegalStatementAdmon1');
        await I.waitForVisible('#solsReviewLegalStatementAdmon2');
        await I.waitForVisible('#solsReviewLegalStatementAdmon3');
        await I.waitForVisible('#solsReviewLegalStatement4');
        await I.see(completeApplicationConfig.page1_AdmonWilllegalStmtLink);
    } else if (willType === 'NoWill') {
        await I.waitForVisible('#solsReviewLegalStatement1');
        await I.waitForVisible('#solsReviewLegalStatement2');
        await I.waitForVisible('#solsReviewLegalStatement3');
        await I.waitForVisible('#solsReviewLegalStatement4');
        await I.see(completeApplicationConfig.page1_NoWilllegalStmtLink);
    } else {
        await I.waitForVisible('#solsReviewLegalStatement1');
        await I.waitForVisible('#solsReviewLegalStatement2');
        await I.waitForVisible('#solsReviewLegalStatement3');
        await I.waitForVisible('#solsReviewLegalStatement4');
        await I.see(completeApplicationConfig.page1_legalStmtLink);
    }

    await I.click(`#solsSOTNeedToUpdate-${completeApplicationConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
