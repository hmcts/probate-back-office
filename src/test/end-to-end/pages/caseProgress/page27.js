'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case stopped
module.exports = async function (caseRef) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForElement({css: 'select option[value="8: Object"]'});
    await I.selectOption('select', '8: Object');
    await I.waitForNavigationToComplete(commonConfig.goButton); 
};
