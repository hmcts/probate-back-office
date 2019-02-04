'use strict';

const testConfig = require('src/test/config.js');
const checkYourAnswersConfig = require('./checkYourAnswersConfig.json');

module.exports = function () {

        const I = this;
        I.waitForText(checkYourAnswersConfig.waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#field-trigger-summary', checkYourAnswersConfig.eventSummary);
        I.fillField('#field-trigger-description', checkYourAnswersConfig.eventdescription);

        I.waitForNavigationToComplete(checkYourAnswersConfig.locator);
};
