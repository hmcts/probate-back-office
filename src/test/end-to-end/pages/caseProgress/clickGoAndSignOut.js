'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW event summary and description and final confirm case printed
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    //await I.waitForElement({css: '#field-trigger-summary'});
    await I.waitForElement({css: commonConfig.goButton});
    await I.waitForNavigationToComplete(commonConfig.goButton);
    await I.wait(5);
    await I.signOut();
};
