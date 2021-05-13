'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const completeApplicationConfig = require('./completeApplication');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#extraCopiesOfGrant');
    await I.runAccessibilityTest();
    await I.fillField('#extraCopiesOfGrant', completeApplicationConfig.page5_extraCopiesUK);
    await I.fillField('#outsideUKGrantCopies', completeApplicationConfig.page5_outsideUKGrantCopies);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
