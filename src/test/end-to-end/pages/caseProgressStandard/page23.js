'use strict';
const testConfig = require('src/test/config.js');

// CW - navigate to case
module.exports = async function (caseRef) {
    const I = this;
    
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForElement({xpath: '//select[@id="wb-case-type"]/option[text()="Grant of representation"]'});
    await I.amOnPage(`${testConfig.TestBackOfficeUrl}/v2/case/${await I.replaceAll(caseRef,'-','')}`);
    await I.waitForNavigationToComplete();
};
