'use strict';
const testConfig = require('src/test/config.js');

// Solicitor - navigate back to case and check complete
module.exports = async function (caseRef) {
    const I = this;
    const selectId = '#wb-case-state';
    const optText = 'Grant issued';

    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    // make sure case stopped is available in dropdown
    await I.waitForElement({css: selectId});
    // make sure this is a selectable option
    await I.selectOption(selectId, optText); 

    await I.amOnPage(`${testConfig.TestBackOfficeUrl}/v2/case/${await I.replaceAll(caseRef,'-','')}`);
    await I.waitForNavigationToComplete();
};
