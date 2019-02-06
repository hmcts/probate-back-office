'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig.json');

module.exports = function (jurisdiction, caseType, event) {

        const I = this;
        I.waitForText(createCaseConfig.waitForText, testConfig.TestTimeToWaitForText);
      //  I.amOnPage(createCaseConfig.pageUrl);

        I.wait(10);
        I.selectOption(createCaseConfig.list1_id, jurisdiction);
        I.selectOption(createCaseConfig.list2_id, caseType);
        I.selectOption(createCaseConfig.list3_id, event);

        I.waitForNavigationToComplete(createCaseConfig.locator);
};
