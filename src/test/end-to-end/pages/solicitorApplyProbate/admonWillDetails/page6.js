'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// Only gets invoked on admon will failure path
module.exports = async function () {
    const I = this;
    await I.waitForElement('#confirmation-body');
    await I.runAccessibilityTest();

    await I.waitForNavigationToComplete(commonConfig.submitButton);
};
