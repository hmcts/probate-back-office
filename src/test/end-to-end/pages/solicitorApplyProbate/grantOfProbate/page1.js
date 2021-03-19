'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#willAccessOriginal');
    await I.runAccessibilityTest();

    await I.click(`#willAccessOriginal-${grantOfProbateConfig.optionYes}`);

    await I.fillField('#originalWillSignedDate-day', grantOfProbateConfig.page1_originalWillSignedDate_day);
    await I.fillField('#originalWillSignedDate-month', grantOfProbateConfig.page1_originalWillSignedDate_month);
    await I.fillField('#originalWillSignedDate-year', grantOfProbateConfig.page1_originalWillSignedDate_year);

    await I.click(`#willHasCodicils-${grantOfProbateConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
