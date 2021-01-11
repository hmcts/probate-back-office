'use strict';

const intestacyDetailsConfig = require('./intestacyDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsAdditionalInfo');
    await I.fillField('#solsAdditionalInfo', intestacyDetailsConfig.page2_applicationNotes);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
