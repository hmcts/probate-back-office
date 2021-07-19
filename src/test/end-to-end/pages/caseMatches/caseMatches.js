'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, nextStepName, retainFirstItem=true, addNewButtonLocator=null, skipMatchingInfo=false) {

    const I = this;
    await I.waitForText(nextStepName, testConfig.WaitForTextTimeout);

    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);

    const btnLocator = {css: 'button.button-secondary[aria-label^="Remove Possible case matches"]'};
    const actionBtnLocator = {css: 'button.action-button[title="Remove"]'};
    await I.wait(testConfig.CaseMatchesInitialDelay);

    const numOfElements = await I.grabNumberOfVisibleElements(btnLocator);

    if (numOfElements > 0) {
        await I.waitForElement('#caseMatches_0_0', testConfig.WaitForTextTimeout);
        await I.waitForVisible({css: '#caseMatches_0_valid_Yes'}, testConfig.WaitForTextTimeout);
    }
    // -1 to ignore previous button at bottom of page
    /* eslint-disable no-await-in-loop */
    const btnLocatorLastChild = {css: `${btnLocator.css}:last-child`};
    for (let i = retainFirstItem ? 1 : 0; i < numOfElements; i++) {
        await I.scrollTo(btnLocatorLastChild);
        await I.wait(testConfig.CaseMatchesLocateRemoveButtonDelay);

        await I.waitForEnabled(btnLocatorLastChild);
        await I.click(btnLocatorLastChild);
        // Just a small delay - occasionally we get issues here but only relevant for local dev.
        // Only necessary where we have no auto delay (local dev).

        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(testConfig.ManualDelayShort);
        }

        await I.wait(testConfig.ManualDelayMedium);
        await I.waitForEnabled(actionBtnLocator);
        await I.click(actionBtnLocator);
        await I.waitForInvisible(actionBtnLocator);
        // Just a small delay - occasionally we get issues here but only relevant for local dev.
        // Only necessary where we have no auto delay (local dev).
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(testConfig.ManualDelayShort);
        }
    }

    if (numOfElements === 0 && retainFirstItem && addNewButtonLocator) {
        await I.click(addNewButtonLocator);
    }

    if (retainFirstItem && (numOfElements > 0 || addNewButtonLocator)) {
        // Just a small delay - occasionally we get issues here but only relevant for local dev.
        // Only necessary where we have no auto delay (local dev).
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.scrollTo({css: 'input[id$="valid_Yes"]'});
        await I.waitForElement({css: 'input[id$="valid_Yes"]'});
        await I.click({css: 'input[id$="valid_Yes"]'});
        await I.waitForElement({css: 'input[id$="doImport_No"]'});
        await I.click({css: 'input[id$="doImport_No"]'});
    }

    await I.waitForElement(commonConfig.continueButton);
    await I.scrollTo({css: commonConfig.continueButton});
    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);

    if (skipMatchingInfo) {
        await I.waitForElement({css: '#field-trigger-summary'});
        await I.waitForEnabled(commonConfig.continueButton);
        // Just a small delay - occasionally we get issues here but only relevant for local dev.
        // Only necessary where we have no auto delay (local dev).
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(testConfig.ManualDelayShort);
        }
        await I.waitForNavigationToComplete(commonConfig.continueButton);
    }
    await I.wait(testConfig.CaseMatchesCompletionDelay);
};
