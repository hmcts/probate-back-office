'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsStartPage');
    await I.waitForNavigationToComplete(commonConfig.goButton);
};
