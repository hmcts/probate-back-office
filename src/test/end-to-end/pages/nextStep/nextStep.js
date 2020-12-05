'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function (nextStep, webDriverWait) {

    const I = this;

    await I.waitForEnabled({css: '#next-step'}, testConfig.TestTimeToWaitForText || 60);
    await I.selectOption('#next-step', nextStep);
    await I.waitForEnabled(commonConfig.goButton, testConfig.TestTimeToWaitForText || 60);
    await I.waitForNavigationToComplete(commonConfig.goButton, webDriverWait);
};
