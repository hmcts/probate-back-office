'use strict';

const testConfig = require('src/test/config.js');
const eventSummaryConfig = require('./eventSummaryConfig.json');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(eventSummaryConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.fillField('#field-trigger-summary', eventSummaryConfig.summary);
    I.fillField('#field-trigger-description', eventSummaryConfig.comment);

    I.waitForNavigationToComplete(eventSummaryConfig.locator);

};
