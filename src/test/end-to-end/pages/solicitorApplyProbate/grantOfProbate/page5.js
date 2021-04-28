'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#furtherEvidenceForApplication');
    await I.runAccessibilityTest();
    await I.fillField('#furtherEvidenceForApplication', grantOfProbateConfig.page5_applicationNotes);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
