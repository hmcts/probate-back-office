'use strict';

const admonWillDetailsConfig = require('./admonWillDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsAdditionalInfo');
    await I.waitForText('Notes for this application (Optional)');
    await I.runAccessibilityTest();
    await I.fillField('#solsAdditionalInfo', admonWillDetailsConfig.page5_applicationNotes);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
