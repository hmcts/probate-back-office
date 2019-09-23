'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, caseMatchesConfig, nextStepName) {

    const I = this;
    caseMatchesConfig.waitForText = nextStepName;
    I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);
    I.seeElement('#caseMatches_0_0');

    /*
    I.seeInField('#caseMatches_0_fullName', caseMatchesConfig.fullname);
    I.seeInField('#caseMatches_0_dob', caseMatchesConfig.dob);
    I.seeInField('#caseMatches_0_postcode', caseMatchesConfig.postcode);
    */

    I.click('#caseMatches_0_valid-Yes');
    I.click('#caseMatches_0_doImport-No');

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
