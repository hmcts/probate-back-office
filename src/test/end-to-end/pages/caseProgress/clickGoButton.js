'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW event summary and description and final confirm case printed
module.exports = async function () {
    const I = this;
    await I.waitForElement({css: commonConfig.goButton});
    await I.waitForNavigationToComplete(commonConfig.goButton);
};
