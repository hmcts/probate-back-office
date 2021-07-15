'use strict';
const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case printed
module.exports = async function (stateOptionText) {
    const I = this;
    if (testConfig.TestForXUI) {
        await I.wait(1);
    }
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement({css: '#next-step'});
    await I.waitForElement({xpath: `//select/option[text()="${stateOptionText}"]`});
    if (testConfig.TestForXUI) {
        await I.wait(1);
    }
    await I.selectOption({css: '#next-step'}, stateOptionText);
    if (testConfig.TestForXUI) {
        await I.wait(1);
    }
    await I.click({css: commonConfig.submitButton});
    if (testConfig.TestForXUI) {
        await I.wait(3);
    }
};
