'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./emailCaveatorConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function () {

    const I = this;

    I.waitForText(emailCaveatorConfig.page1_waitForText, testConfig.TestTimeToWaitForText);

    I.selectOption('#messageContent', emailCaveatorConfig.page1_message_content);

    I.click(commonConfig.continueButton);
};
