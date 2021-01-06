'use strict';

const testConfig = require('src/test/config.js');
const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(completeApplicationConfig.page2_waitForText, testConfig.TestTimeToWaitForText);

    await I.waitForNavigationToComplete(commonConfig.goButton);
};
