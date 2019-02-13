'use strict';

const testConfig = require('src/test/config.js');
const commentConfig = require('./commentConfig.json');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(commentConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.fillField('#field-trigger-summary', commentConfig.summary);
    I.fillField('#field-trigger-description', commentConfig.comment);

    I.waitForNavigationToComplete(commentConfig.continueButton);

};
