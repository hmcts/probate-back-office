'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW issue grant confirmation
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails  
    const locator = {css: '#boSendToBulkPrint-No'};
    await I.waitForElement(locator);
    await I.click(locator) 
    await I.waitForElement({css: commonConfig.continueButton}); 
    await I.waitForNavigationToComplete(commonConfig.continueButton);   
};
