'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// grant of probate details part 12 - final confirmation
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('#confirmation-body');
    I.see('This probate application has now been submitted');
    await I.waitForNavigationToComplete(commonConfig.goButton);
    await I.wait(5);
};
