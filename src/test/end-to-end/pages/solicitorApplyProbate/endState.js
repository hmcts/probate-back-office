'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (endState) {
    const I = this;
    await I.waitForText(endState, testConfig.TestTimeToWaitForText);
};
