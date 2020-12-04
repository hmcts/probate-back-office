'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, caseMatchesConfig, nextStepName) {

    const I = this;
    caseMatchesConfig.waitForText = nextStepName;
    await I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);

    const btnLocator = {css: 'button.button-secondary[aria-label^="Remove Possible case matches"]'};
    const actionBtnLocator = {css: 'button.action-button[title="Remove"]'};
    let numOfElements = await I.grabNumberOfVisibleElements(btnLocator);

    // -1 to ignore previous button at bottom of page

    /* eslint-disable no-await-in-loop */
    const btnLocatorLastChild = {css: `${btnLocator.css}:last-child`};
    for (let i = 0; i < numOfElements; i++) {
        await I.waitForElement(btnLocatorLastChild);
        await I.click(btnLocatorLastChild);
        await I.waitForElement(actionBtnLocator);
        await I.click(actionBtnLocator);
        await I.waitForInvisible(actionBtnLocator);
    }
    
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
