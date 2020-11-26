'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case Mark as ready for examination
module.exports = async function () {
    const I = this;
    const radioLocator = {css: '#boEmailDocsReceivedNotification-No'};

    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails   
    await I.waitForElement(radioLocator);
    await I.click(radioLocator);
    await I.waitForNavigationToComplete(commonConfig.continueButton);  
};
