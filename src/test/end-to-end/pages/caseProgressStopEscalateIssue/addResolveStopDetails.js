'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW confirm case stopped
module.exports = async function () {
    const I = this;
    await I.waitForElement({css: '#resolveStopState'});
    await I.selectOption({css: '#resolveStopState'}, '2: CasePrinted');
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
