'use strict';

const testConfig = require('src/test/config.js');
const caseMatchesConfig = require('../caseMatches/caseMatchesConfig.json');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.seeInField('#caseMatches_0_fullName', caseMatchesConfig.fullname);
    I.seeInField('#caseMatches_0_dob', caseMatchesConfig.dob);
    I.seeInField('#caseMatches_0_postcode', caseMatchesConfig.postcode);

    I.click('#caseMatches_0_valid-Yes');

    I.waitForNavigationToComplete(caseMatchesConfig.continueButton);

};
