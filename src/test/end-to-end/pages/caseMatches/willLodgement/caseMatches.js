'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, caseMatchesConfig, nextStepName) {

    const I = this;
    caseMatchesConfig.waitForText = nextStepName;
    await I.waitForText(caseMatchesConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.waitForText(caseRef);
    await I.waitForElement('#caseMatches_0_0');

    const btnLocator = {css: 'button.button-secondary[aria-label^="Remove Possible case matches"]'};
    const actionBtnLocator = {css: 'button.action-button[title="Remove"]'};
    const numOfElements = await I.grabNumberOfVisibleElements(btnLocator);

    if (numOfElements === 0) {
        await I.click(caseMatchesConfig.addNewButton);
    } else {
        // -1 to ignore previous button at bottom of page
        /* eslint-disable no-await-in-loop */
        const btnLocatorLastChild = {css: `${btnLocator.css}:last-child`};
        for (let i = 0; i < numOfElements - 1; i++) {
            await I.waitForElement(btnLocatorLastChild);
            await I.click(btnLocatorLastChild);
            await I.waitForElement(actionBtnLocator);
            await I.click(actionBtnLocator);
            await I.waitForInvisible(actionBtnLocator);
        }
    }

    await I.waitForEnabled({css: 'input[id$="valid-Yes"'});
    await I.click({css: 'input[id$="valid-Yes"'});
    await I.click({css: 'input[id$="doImport-No"'});

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
