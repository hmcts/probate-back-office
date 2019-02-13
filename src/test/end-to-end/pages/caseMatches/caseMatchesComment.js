'use strict';

const testConfig = require('src/test/config.js');
const caseMatchesCommentConfig = require('./caseMatchesCommentConfig.json');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(caseMatchesCommentConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.fillField('#field-trigger-summary', caseMatchesCommentConfig.summary);
    I.fillField('#field-trigger-description', caseMatchesCommentConfig.comment);

    I.waitForNavigationToComplete(caseMatchesCommentConfig.continueButton);

};
