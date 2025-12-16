'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

// CW select case printed
module.exports = async function (stateOptionText) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForEnabled({css: '#next-step'});
    await I.waitForElement({xpath: `//select/option[text()="${stateOptionText}"]`});
    await I.selectOption({css: '#next-step'}, stateOptionText);
    await I.wait(testConfig.CaseworkerGoButtonClickDelay);
    await I.click({css: commonConfig.submitButton});
};
