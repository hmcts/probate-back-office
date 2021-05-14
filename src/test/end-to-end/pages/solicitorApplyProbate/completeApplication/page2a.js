'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsConfirmSignSOT1');
    await I.runAccessibilityTest();
    await I.waitForText('Confirm your client has agreed with the legal statement and declaration&nbsp;-&nbsp;');
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
