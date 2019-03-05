'use strict';

const testConfig = require('src/test/config.js');
const checkYourAnswersConfig = require('./checkYourAnswersConfig.json');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function () {

    const I = this;
    I.waitForText(checkYourAnswersConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.fillField('#field-trigger-summary', checkYourAnswersConfig.summary);
    I.fillField('#field-trigger-description', checkYourAnswersConfig.comment);

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
