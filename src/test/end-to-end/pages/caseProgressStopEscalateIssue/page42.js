'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW event summary and description and final confirm find matching
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails   
    await I.waitForElement({css: commonConfig.goButton}); 
    await I.waitForNavigationToComplete(commonConfig.goButton);   
};
