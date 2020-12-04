'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, caseMatchesConfig, nextStepName) {

    const I = this;
    caseMatchesConfig.waitForText = nextStepName;
    await I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);

    const btnLocator = {css: 'button.button-secondary'};
    const actionBtnLocator = {css: 'button.action-button[title="Remove"]'};

    let numOfElements = await I.grabNumberOfVisibleElements(btnLocator); // await I.getNumElements(btnLocator);
    
    // -1 to ignore previous button at bottom of page
    for (let i = 0; i < numOfElements - 1; i++ ) {    
       await I.waitForVisible(btnLocator);
       await I.click(btnLocator);
       await I.waitForElement(actionBtnLocator);
       await I.click(actionBtnLocator);
       await I.waitForInvisible(actionBtnLocator);
    }
        
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
