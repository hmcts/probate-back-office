'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#dispenseWithNotice');

    await I.click(`#dispenseWithNotice-${grantOfProbateConfig.optionYes}`);
    await I.selectOption('#titleAndClearingType', grantOfProbateConfig.page2_titleAndClearingType);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
