'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case escalated to registrar
module.exports = async function (caseRef) {
    const I = this;
    const locator = {xpath: '//select/option[text()="Escalate to registrar"]'};

    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForElement(locator);
    await I.selectOption('select', 'Escalate to registrar');
    await I.waitForNavigationToComplete(commonConfig.goButton);  
};
