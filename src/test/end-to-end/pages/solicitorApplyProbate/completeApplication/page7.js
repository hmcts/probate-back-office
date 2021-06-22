'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page7_waitForText);
    await I.runAccessibilityTest();
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
