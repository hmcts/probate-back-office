'use strict';

const testConfig = require('src/test/config.js');
const selectCaseConfig = require('./selectCaseConfig.json');

module.exports = function () {

    const I = this;
    I.waitForText('Only one dummy case needs to be created for the purpose of searching legacy data', testConfig.TestTimeToWaitForText);
    //I.waitForTe(10);
    I.waitForNavigationToComplete(selectCaseConfig.caseLink);
};
