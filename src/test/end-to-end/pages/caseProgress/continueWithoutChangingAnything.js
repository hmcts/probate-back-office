'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function (numTimes=1) {
    const I = this;

    /* eslint-disable no-await-in-loop */
    for (let i=0; i < numTimes; i++) {
        await I.waitForElement({css: commonConfig.continueButton});
        await I.waitForNavigationToComplete(commonConfig.continueButton,
            testConfig.CaseProgressContinueWithoutChangingDelay);
    }
};
