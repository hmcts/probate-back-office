'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#willAccessOriginal');
    await I.click(`#willAccessOriginal-${grantOfProbateConfig.optionYes}`);
    await I.click(`#willHasCodicils-${grantOfProbateConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
