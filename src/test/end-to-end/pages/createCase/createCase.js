'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig.json');

module.exports = function (jurisdiction, caseType, event) {

        const I = this;
        I.waitForText(createCaseConfig.waitForText, testConfig.TestTimeToWaitForText);
      //  I.amOnPage(createCaseConfig.pageUrl);

        I.selectOption(createCaseConfig.lists.list1.id, jurisdiction);
        I.wait(10);
        I.selectOption(createCaseConfig.lists.list2.id, caseType);
        I.selectOption(createCaseConfig.lists.list3.id, event);

        I.waitForNavigationToComplete(createCaseConfig.locator);
};
