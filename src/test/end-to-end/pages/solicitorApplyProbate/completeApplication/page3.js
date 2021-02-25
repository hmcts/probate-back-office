'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#extraCopiesOfGrant');
    await I.runAccessibilityTest();
    await I.fillField('#extraCopiesOfGrant', completeApplicationConfig.page3_extraCopiesUK);
    await I.fillField('#outsideUKGrantCopies', completeApplicationConfig.page3_outsideUKGrantCopies);

    await I.waitForElement('#solsConfirmSignSOT1');
    // await I.runAccessibilityTest();

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
