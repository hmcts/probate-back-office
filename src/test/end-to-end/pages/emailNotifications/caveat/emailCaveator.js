'use strict';

const testConfig = require('src/test/config');
const emailCaveatorConfig = require('./emailCaveatorConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {

    const I = this;
    await I.waitForText(emailCaveatorConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);

    await I.fillField('#messageContent', emailCaveatorConfig.email_message_content);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
