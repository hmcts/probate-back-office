'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW set case state to Find matches(Issue grant)
module.exports = async function () {
    const I = this;
    const btnLocator = {css: 'button.button-secondary'}; // [aria-label="Remove Possible case matches"]
    const actionBtnLocator = {css: 'button.action-button[title="Remove"]'};

    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForText('Find matches (Issue grant)');
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