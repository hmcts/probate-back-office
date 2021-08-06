'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#willAccessOriginal');
    await I.runAccessibilityTest();

    await I.click(`#willAccessOriginal_${grantOfProbateConfig.optionYes}`);
    await I.click(`#willHasCodicils_${grantOfProbateConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
