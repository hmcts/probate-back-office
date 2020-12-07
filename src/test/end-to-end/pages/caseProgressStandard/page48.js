'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case Mark as ready to issue
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails   
    await I.waitForElement({css: '#field-trigger-summary'});
    await I.waitForNavigationToComplete(commonConfig.goButton);
    
    await I.waitForElement({css: '#sign-out'});
    await I.waitForNavigationToComplete('#sign-out'); 
};
