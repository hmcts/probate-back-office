'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW issue grant submmit
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails   
    await I.waitForElement({css: commonConfig.goButton}); 
    await I.waitForNavigationToComplete(commonConfig.goButton);   

    await I.waitForElement({css: '#sign-out'});
    await I.click({css: '#sign-out'});
    await I.waitForNavigationToComplete();
};
