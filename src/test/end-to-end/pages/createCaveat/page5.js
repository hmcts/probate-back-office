'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'import') {
        I.waitForText(createCaveatConfig.page5_amend_waitForText, testConfig.TestTimeToWaitForText);
    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};