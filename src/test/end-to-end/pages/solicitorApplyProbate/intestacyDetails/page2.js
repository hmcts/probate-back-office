'use strict';

const intestacyDetailsConfig = require('./intestacyDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsMinorityInterest');
    await I.runAccessibilityTest();

    await I.click(`#solsApplicantRelationshipToDeceased-${intestacyDetailsConfig.page2_child}`);
    await I.click(`#solsApplicantSiblings_${intestacyDetailsConfig.optionNo}`);
    await I.click(`#deceasedMaritalStatus-${intestacyDetailsConfig.page2_maritalstatus}`);
    await I.click(`#solsMinorityInterest_${intestacyDetailsConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
