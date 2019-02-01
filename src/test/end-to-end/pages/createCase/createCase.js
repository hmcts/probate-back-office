'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig.json');

module.exports = function () {

        const I = this;
        I.waitForText(createCaseConfig.waitForText, testConfig.TestTimeToWaitForText);
      //  I.amOnPage(createCaseConfig.pageUrl);

        I.selectOption(createCaseConfig.lists.list1.id, createCaseConfig.lists.list1.text);
        I.wait(3);
        I.selectOption(createCaseConfig.lists.list2.id, createCaseConfig.lists.list2.text);
        I.selectOption(createCaseConfig.lists.list3.id, createCaseConfig.lists.list3.text);

        I.waitForNavigationToComplete(createCaseConfig.locator);
};
