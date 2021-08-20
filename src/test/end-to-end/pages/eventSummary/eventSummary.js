'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');

module.exports = async function (caseRef, nextStepName) {

    const I = this;
    await I.wait(testConfig.EventSummaryDelay);
    let eventSummaryPrefix = nextStepName;

    await I.waitForText(nextStepName, testConfig.WaitForTextTimeout);

    await I.see(caseRef);

    eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

    await I.waitForElement('#field-trigger-summary');

    await I.fillField('#field-trigger-summary', eventSummaryPrefix + eventSummaryConfig.summary);
    await I.fillField('#field-trigger-description', eventSummaryPrefix + eventSummaryConfig.comment);

    await I.waitForNavigationToComplete(commonConfig.continueButton);

};
