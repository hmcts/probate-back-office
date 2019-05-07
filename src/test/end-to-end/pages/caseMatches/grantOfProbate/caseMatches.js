'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, caseMatchesConfig, nextStepName) {

    const I = this;
    caseMatchesConfig.waitForText = nextStepName;
    I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);
    I.seeInField('#caseMatches_1_fullName', caseMatchesConfig.fullname);
    I.seeInField('#caseMatches_1_dob', caseMatchesConfig.dob);
    I.seeInField('#caseMatches_1_postcode', caseMatchesConfig.postcode);

    I.click('#caseMatches_1_valid-Yes');

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
