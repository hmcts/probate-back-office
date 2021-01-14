'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, caseMatchesConfig, nextStepName, retainFirstItem=true, addNewItem=false) {

    const I = this;
    caseMatchesConfig.waitForText = nextStepName;
    await I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.waitForText(caseRef, testConfig.TestTimeToWaitForText);

    const btnLocator = {css: 'button.button-secondary[aria-label^="Remove Possible case matches"]'};
    const actionBtnLocator = {css: 'button.action-button[title="Remove"]'};
    const numOfElements = await I.grabNumberOfVisibleElements(btnLocator);

    // -1 to ignore previous button at bottom of page
    /* eslint-disable no-await-in-loop */
    const btnLocatorLastChild = {css: `${btnLocator.css}:last-child`};
    for (let i = retainFirstItem ? 1 : 0; i < numOfElements; i++) {
        await I.waitForElement(btnLocatorLastChild);
        await I.click(btnLocatorLastChild);
        await I.waitForElement(actionBtnLocator);
        await I.click(actionBtnLocator);
        await I.waitForInvisible(actionBtnLocator);
    }

    if (numOfElements === 0 && retainFirstItem && addNewItem) {
        await I.click(caseMatchesConfig.addNewButton);
    }

    if (numOfElements > 0 || (retainFirstItem && addNewItem)) {        
        await I.scrollTo({css: 'input[id$="valid-Yes"]'});
        await I.waitForClickable({css: 'input[id$="valid-Yes"]'});
        await I.click({css: 'input[id$="valid-Yes"]'});
        await I.waitForClickable({css: 'input[id$="doImport-No"]'});
        await I.click({css: 'input[id$="doImport-No"]'});    
    }

    if (!testConfig.TestAutoDelayEnabled) {
        // if auto delay off in local dev env, occasionally get issues
        await I.wait(0.25);
    }
    
    await I.scrollTo({css: commonConfig.continueButton});
    await I.waitForClickable(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
