'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, caseMatchesConfig) {

    const I = this;
    I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);
    I.seeInField('#caseMatches_51_fullName', caseMatchesConfig.fullname);
    I.seeInField('#caseMatches_51_dob', caseMatchesConfig.dob);
    I.seeInField('#caseMatches_51_postcode', caseMatchesConfig.postcode);

    I.click('#caseMatches_51_valid-Yes');

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
