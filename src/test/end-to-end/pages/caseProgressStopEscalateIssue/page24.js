'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW select case printed
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForElement({css: 'select option[value="1: Object"]'});
    await I.selectOption('select', '1: Object');
    await I.click({css: commonConfig.goButton});    
};
