'use strict';
const assert = require('assert');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// grant of probate details part 7 - confirmation
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('h1.heading-h1');
    const headingHtml = await I.grabAttributeFrom('h1.heading-h1 > span', 'innerHTML');
    assert (headingHtml === 'Confirm your client has agreed with the legal statement and declaration&nbsp;-&nbsp;');
    await I.waitForNavigationToComplete(commonConfig.continueButton);    
};
