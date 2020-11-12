'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW set state to issue grant
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails   
    const optText = 'Issue grant'; 
    await I.waitForElement({xpath: `//select/option[text()="${optText}"]`});
    await I.selectOption({css: '#next-step'}, optText);
    await I.waitForNavigationToComplete(commonConfig.goButton);  
};
