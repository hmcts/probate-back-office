'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#extraCopiesOfGrant');
    await I.fillField('#extraCopiesOfGrant', completeApplicationConfig.page4_extraCopiesUK);
    await I.fillField('#outsideUKGrantCopies', completeApplicationConfig.page4_outsideUKGrantCopies);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
