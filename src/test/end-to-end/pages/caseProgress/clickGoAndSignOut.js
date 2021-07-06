'use strict';
const testConfig = require('src/test/config');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW event summary and description and final confirm case printed
module.exports = async function () {
    const I = this;

    await I.waitForElement({css: '#field-trigger-summary'});
    await I.waitForElement({css: commonConfig.goButton});
    await I.waitForNavigationToComplete(commonConfig.goButton, true);
    await I.waitForNavigationToComplete(testConfig.XuiSignoutCssSelector);
};
