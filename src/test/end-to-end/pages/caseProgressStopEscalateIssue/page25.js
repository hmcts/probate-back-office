'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW confirm case printed
module.exports = async function (caseRef) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForElement({css: '#casePrinted'});
    await I.selectOption({css: '#casePrinted'}, '1: Yes');
    await I.waitForNavigationToComplete(commonConfig.continueButton);   
};
