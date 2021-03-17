'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, caseMatchesConfig, nextStepName, retainFirstItem=true, addNewItem=false) {

    const I = this;
    caseMatchesConfig.waitForText = nextStepName;
    await I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.waitForText(caseRef, testConfig.TestTimeToWaitForText);

    await I.waitForElement('#caseMatches_0_0', testConfig.TestTimeToWaitForText);

    await I.waitForVisible({css: '#caseMatches_0_valid-Yes'}, testConfig.TestTimeToWaitForText);

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

    await I.waitForEnabled({css: 'input[id$="valid-Yes"]'});
    await I.click({css: 'input[id$="valid-Yes"]'});
    await I.click({css: 'input[id$="doImport-No"]'});

    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
