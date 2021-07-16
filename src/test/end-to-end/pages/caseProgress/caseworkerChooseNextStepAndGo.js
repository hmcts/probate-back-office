'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case printed
module.exports = async function (stateOptionText, delay = 3) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement({css: '#next-step'});
    await I.waitForElement({xpath: `//select/option[text()="${stateOptionText}"]`});
    await I.selectOption({css: '#next-step'}, stateOptionText);
    await I.click({css: commonConfig.submitButton});
    await I.wait(delay);
};
