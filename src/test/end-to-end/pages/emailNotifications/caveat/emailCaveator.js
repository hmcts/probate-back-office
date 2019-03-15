'use strict';

const testConfig = require('src/test/config');
const emailCaveatorConfig = require('./emailCaveatorConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(emailCaveatorConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.fillField('#messageContent', emailCaveatorConfig.email_message_content);

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
