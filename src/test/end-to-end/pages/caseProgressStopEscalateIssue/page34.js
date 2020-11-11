'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case escalated to registrar
module.exports = async function (caseRef) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails   
    const optText = 'Escalate to registrar'; 
    await I.waitForElement({xpath: `//select/option[text()="${optText}"]`});
    await I.selectOption('select', optText);
    await I.waitForNavigationToComplete(commonConfig.goButton);  
};
