'use strict';
const assert = require('assert');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('h1.heading-h1');
    await I.waitForElement('#solsConfirmSignSOT1');
    await I.runAccessibilityTest();
    const headingHtml = await I.grabAttributeFrom('h1.heading-h1 > span', 'innerHTML');
    assert (headingHtml === 'Confirm your client has agreed with the legal statement and declaration&nbsp;-&nbsp;');
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
