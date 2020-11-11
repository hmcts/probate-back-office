'use strict';
const assert = require('assert');
const testConfig = require('src/test/config.js');

// Solicitor - navigate back to case
module.exports = async function (caseRef) {
    const I = this;
    const selectId = '#wb-case-state';
    const optVal = '11: Object'
    const locator = {css: `${selectId} option[value="${optVal}"]`};

    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    // make sure case stopped is available in dropdown
    
    await I.waitForElement(locator);
    // make sure this is a selectable option
    await I.selectOption(selectId, optVal); 
    const optValHtml = await I.grabAttributeFrom(locator, 'innerHTML');
    assert(optValHtml === 'Case stopped');


    await I.amOnPage(`${testConfig.TestBackOfficeUrl}/v2/case/${await I.replaceAll(caseRef,'-','')}`);
    await I.waitForNavigationToComplete();

};
