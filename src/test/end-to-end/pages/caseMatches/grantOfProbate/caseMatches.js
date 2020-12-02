'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, caseMatchesConfig, nextStepName) {

    const I = this;
    caseMatchesConfig.waitForText = nextStepName;
    await I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);
    await I.seeElement('#caseMatches_0_0');

    /*
    I.seeInField('#caseMatches_0_fullName', caseMatchesConfig.fullname);
    I.seeInField('#caseMatches_0_dob', caseMatchesConfig.dob);
    I.seeInField('#caseMatches_0_postcode', caseMatchesConfig.postcode);
    */

    await I.click('#caseMatches_0_valid-Yes');
    await I.click('#caseMatches_0_doImport-No');

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
