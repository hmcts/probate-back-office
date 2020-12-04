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

    await I.waitForVisible({css: '#caseMatches_0_valid-Yes'});

    const btnLocator = {css: 'button.button-secondary'};
    const actionBtnLocator = {css: 'button.action-button[title="Remove"]'};
    let numOfElements = await I.grabNumberOfVisibleElements(btnLocator); // await I.getNumElements(btnLocator);

    // -1 to ignore previous button at bottom of page
    for (let i = 1; i < numOfElements - 1; i++ ) {    
        await I.waitForVisible(btnLocator);
        await I.click(btnLocator);
        await I.waitForElement(actionBtnLocator);
        await I.click(actionBtnLocator);
        await I.waitForInvisible(actionBtnLocator);
    }

    await I.click('#caseMatches_0_valid-Yes');
    await I.click('#caseMatches_0_doImport-No');

    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
