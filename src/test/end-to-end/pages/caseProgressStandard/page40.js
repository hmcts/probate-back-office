'use strict';
const testConfig = require('src/test/config.js');

// Case worker - nav back to case
module.exports = async function (caseRef) {
    const I = this;
    
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForText('Grant of representation', 2, '#wb-case-type option');
    await I.amOnPage(`${testConfig.TestBackOfficeUrl}/v2/case/${await I.replaceAll(caseRef,'-','')}`);
    await I.waitForNavigationToComplete();
};
