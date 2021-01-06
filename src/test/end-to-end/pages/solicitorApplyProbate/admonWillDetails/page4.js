'use strict';

const testConfig = require('src/test/config.js');
const admonWillDetailsConfig = require('./admonWillDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(admonWillDetailsConfig.page4_waitForText, testConfig.TestTimeToWaitForText);
    await I.waitForNavigationToComplete(commonConfig.goButton);
};
