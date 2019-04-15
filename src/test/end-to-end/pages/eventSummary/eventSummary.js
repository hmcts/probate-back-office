'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');

module.exports = function (caseRef, nextStepName) {

    const I = this;

    let eventSummaryPrefix = nextStepName;

    I.waitForText(nextStepName, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

    I.fillField('#field-trigger-summary', eventSummaryPrefix + eventSummaryConfig.summary);
    I.fillField('#field-trigger-description', eventSummaryPrefix + eventSummaryConfig.comment);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
