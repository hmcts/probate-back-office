'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// deceased details part 2
module.exports = async function (caseProgressConfig) {
    const I = this;
    await I.waitForText(caseProgressConfig.ihtHmrcLetterNotReceived);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
