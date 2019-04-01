'use strict';

const testConfig = require('src/test/config.js');
const filterCaseConfig = require('./filterCaseConfig.json');

module.exports = function (jurisdiction, caseType, event) {

    const I = this;
    I.waitForText(filterCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.wait(20);
    I.selectOption(filterCaseConfig.jurisdictionSelection, jurisdiction);
    I.selectOption(filterCaseConfig.caseTypeSelection, caseType);
    I.selectOption(filterCaseConfig.caseStateSelection, event);

    I.waitForNavigationToComplete(filterCaseConfig.applyButton);
};
