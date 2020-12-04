'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const checkYourAnswersConfig = require('./checkYourAnswersConfig.json');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');

module.exports = async function (nextStepName) {

    const I = this;

    let eventSummaryPrefix = nextStepName;

    await I.waitForText(checkYourAnswersConfig.waitForText, testConfig.TestTimeToWaitForText);

    eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

    await I.fillField('#field-trigger-summary', eventSummaryPrefix + eventSummaryConfig.summary);
    await I.fillField('#field-trigger-description', eventSummaryPrefix + eventSummaryConfig.comment);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
