'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, nextStepName, retainFirstItem=true, addNewButtonLocator=null, skipMatchingInfo=false) {

    const I = this;
    await I.waitForText(nextStepName, testConfig.TestTimeToWaitForText);

    await I.waitForText(caseRef, testConfig.TestTimeToWaitForText);

    const btnLocator = {css: 'button.button-secondary[aria-label^="Remove Possible case matches"]'};
    const actionBtnLocator = {css: 'button.action-button[title="Remove"]'};
    // just a small delay - occasionally we get issues.
    // Only necessary where we have no auto delay (local dev).
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.5);
    }
    const numOfElements = await I.grabNumberOfVisibleElements(btnLocator);

    if (numOfElements > 0) {
        await I.waitForElement('#caseMatches_0_0', testConfig.TestTimeToWaitForText);
        await I.waitForVisible({css: '#caseMatches_0_valid-Yes'}, testConfig.TestTimeToWaitForText);
    }

    // -1 to ignore previous button at bottom of page
    /* eslint-disable no-await-in-loop */
    const btnLocatorLastChild = {css: `${btnLocator.css}:last-child`};
    for (let i = retainFirstItem ? 1 : 0; i < numOfElements; i++) {
        await I.waitForEnabled(btnLocatorLastChild);
        await I.click(btnLocatorLastChild);
        // Just a small delay - occasionally we get issues here but only relevant for local dev.
        // Only necessary where we have no auto delay (local dev).
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(0.25);
        }
        await I.waitForEnabled(actionBtnLocator);
        await I.click(actionBtnLocator);
        await I.waitForInvisible(actionBtnLocator);
        // Just a small delay - occasionally we get issues here but only relevant for local dev.
        // Only necessary where we have no auto delay (local dev).
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(0.25);
        }
    }

    if (numOfElements === 0 && retainFirstItem && addNewButtonLocator) {
        await I.click(addNewButtonLocator);
    }

    if (retainFirstItem && (numOfElements > 0 || addNewButtonLocator)) {
        // Just a small delay - occasionally we get issues here but only relevant for local dev.
        // Only necessary where we have no auto delay (local dev).
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(0.5);
        }
        await I.scrollTo({css: 'input[id$="valid-Yes"]'});
        await I.waitForElement({css: 'input[id$="valid-Yes"]'});
        await I.click({css: 'input[id$="valid-Yes"]'});
        await I.waitForElement({css: 'input[id$="doImport-No"]'});
        await I.click({css: 'input[id$="doImport-No"]'});
    }

    await I.waitForElement(commonConfig.continueButton);
    await I.scrollTo({css: commonConfig.continueButton});
    await I.waitForClickable(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);

    if (skipMatchingInfo) {
        await I.waitForElement({css: '#field-trigger-summary'});
        await I.waitForElement(commonConfig.continueButton);
        await I.waitForClickable(commonConfig.continueButton);
        // Just a small delay - occasionally we get issues here but only relevant for local dev.
        // Only necessary where we have no auto delay (local dev).
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(0.25);
        }
        await I.waitForNavigationToComplete(commonConfig.continueButton);
    }
};
