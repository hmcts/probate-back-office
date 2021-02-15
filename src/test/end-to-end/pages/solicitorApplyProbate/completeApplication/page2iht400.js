'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const completeApplicationConfig = require('./completeApplication');

module.exports = async function () {
    const I = this;
    await I.runAccessibilityTest();
pause();
    await I.fillField('#solsIHT400Date-day', completeApplicationConfig.page2_iht400_day);
    await I.fillField('#solsIHT400Date-month', completeApplicationConfig.page2_iht400_month);
    await I.fillField('#solsIHT400Date-year', completeApplicationConfig.page2_iht400_year);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
