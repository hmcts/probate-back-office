'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// Select No for HMRC Letter received
module.exports = async function () {
    const I = this;
    await I.waitForElement('#deceasedForenames');
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
