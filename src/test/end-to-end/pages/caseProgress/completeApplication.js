'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText('Complete application');
    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
