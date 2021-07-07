'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case printed
module.exports = async function (stateOptionText) {
    const I = this;
    console.info('1');
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement({css: '#next-step'});
    console.info('2');
    await I.waitForElement({xpath: `//select/option[text()="${stateOptionText}"]`});
    console.info('3');
    await I.selectOption({css: '#next-step'}, stateOptionText);
    console.info('4');
    await I.click({css: commonConfig.goButton});
};
