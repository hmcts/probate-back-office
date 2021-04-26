'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const completeApplicationConfig = require('./completeApplication');

module.exports = async function () {
    const I = this;

    await I.waitForElement('#solsSOTForenames');
    await I.runAccessibilityTest();
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
