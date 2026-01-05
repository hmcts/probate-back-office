'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW confirm case stopped
module.exports = async function (chooseState) {
    const I = this;
    await I.waitForElement({css: '#resolveStopState'});
    await I.selectOption({css: '#resolveStopState'}, chooseState);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
