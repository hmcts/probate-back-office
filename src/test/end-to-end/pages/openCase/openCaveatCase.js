'use strict';

const testConfig = require('src/test/config.js');
const openCaveatCaseConfig = require('./openCaveatCaseConfig.json');

module.exports = function () {

    const I = this;
    I.waitForText(openCaveatCaseConfig.waitForText, testConfig.TestTimeToWaitForText);
    I.click(openCaveatCaseConfig.firstCaseInList);
};