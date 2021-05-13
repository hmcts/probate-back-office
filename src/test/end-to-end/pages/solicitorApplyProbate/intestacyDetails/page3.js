'use strict';

const intestacyDetailsConfig = require('./intestacyDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#furtherEvidenceForApplication');
    await I.runAccessibilityTest();

    await I.fillField('#furtherEvidenceForApplication', intestacyDetailsConfig.page3_applicationNotes);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
