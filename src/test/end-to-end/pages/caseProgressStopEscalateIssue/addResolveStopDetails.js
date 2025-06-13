'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW confirm case stopped
module.exports = async function () {
    const I = this;
    await I.waitForElement({css: '#resolveStopState'});
    await I.selectOption('#resolveStopState', 'Awaiting documentation');
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
