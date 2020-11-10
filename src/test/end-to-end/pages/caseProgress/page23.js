'use strict';
const testConfig = require('src/test/config.js');

// CW - navigate to case
module.exports = async function (caseRef) {
    const I = this;
    
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForText('Grant of representation', 2, '#wb-case-type option');

    /*
    This gets complex if there is > 1 page of cases.
    However, we are not testing the case search here - that is tested elsewhere.
    This gives us the right to bypass this and simulate the user typing in the url to the case they are interested in.

    await I.selectOption('#wb-case-type', '1: Object');

    await I.selectOption('#wb-case-state', '');
    await I.click({css: 'button:not(.button-secondary)'});
    
    const locator = `a[aria-label="go to case with Case reference:${caseRef}"]`;
    await I.waitForElement({css: locator});
    await I.click({css: locator});
    */
    await I.navigateToPage(`${testConfig.TestBackOfficeUrl}/v2/case/${await I.replaceAll(caseRef,'-','')}`);

};
