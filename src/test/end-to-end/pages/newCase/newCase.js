'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig.json');

module.exports = function () {

        const I = this;
        I.waitForText(newCaseConfig.waitForText,testConfig.TestTimeToWaitForText);
      //  I.amOnPage(newCaseConfig.pageUrl);

        I.waitForNavigationToComplete(newCaseConfig.locator);
};
