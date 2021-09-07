'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;

    await I.waitForText('Check your answers', testConfig.WaitForTextTimeout);
    await I.runAccessibilityTest();
    await I.wait(3);

    await I.waitForNavigationToComplete2(commonConfig.continueButton, 3);
};
