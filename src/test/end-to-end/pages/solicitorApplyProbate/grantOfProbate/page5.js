'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsAdditionalInfo');
    await I.fillField('#solsAdditionalInfo', grantOfProbateConfig.page4_applicationNotes);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
