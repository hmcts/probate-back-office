'use strict';

const testConfig = require('src/test/config.cjs');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, nextStepName) {

    const I = this;
    await I.waitForText(nextStepName, testConfig.WaitForTextTimeout);

    await I.waitForText(caseRef, testConfig.WaitForTextTimeout);

    const btnLocator = {css: 'button.button-secondary[aria-label^="Remove Possible case matches"]'};

    await I.wait(testConfig.CaseMatchesInitialDelay);

    const numOfElements = await I.grabNumberOfVisibleElements(btnLocator);

    if (numOfElements > 0) {
        await I.waitForElement('#caseMatches_0_0', testConfig.WaitForTextTimeout);
        await I.waitForVisible({css: '#caseMatches_0_valid_Yes'}, testConfig.WaitForTextTimeout);
    }
    const legacyApplication = {css: '#caseMatches_%s_%s > fieldset > ccd-field-read:nth-child(2) > div > ccd-field-read-label > div > dl > dd'};
    const legacyApplicationTypeText = 'Legacy LEGACY APPLICATION';
    const removeBtnLocator = {css: '#caseMatches_%s_%s > div > div.float-right > button'};
    const caseMatchesValidYesLocator = {css: '#caseMatches_%s_valid_Yes'};
    const caseMatchesImportNoLocator = {css: '#caseMatches_%s_doImport_No'};
    let i = numOfElements;
    await I.logInfo('The number of cases is : ', i);
    /* eslint-disable no-await-in-loop */
    while (i > 1) {
        const removeBtnLocatorNthChild = removeBtnLocator.css.replace(/%s/g, i-1);
        const caseMatchesValidYesLocatorNthChild = caseMatchesValidYesLocator.css.replace(/%s/g, i-1);
        const caseMatchesImportNoLocatorNthChild = caseMatchesImportNoLocator.css.replace(/%s/g, i-1);
        await I.scrollTo(removeBtnLocatorNthChild);
        await I.wait(testConfig.CaseMatchesLocateRemoveButtonDelay);
        await I.waitForEnabled(removeBtnLocatorNthChild);
        await I.waitForEnabled(legacyApplication.css.replace(/%s/g, i-1));
        const text = await I.grabTextFrom(legacyApplication.css.replace(/%s/g, i-1));
        if (text === legacyApplicationTypeText) {
            // eslint-disable-next-line no-unused-vars
            if (!testConfig.TestAutoDelayEnabled) {
                await I.wait(testConfig.ManualDelayMedium);
            }
            await I.waitForElement(caseMatchesValidYesLocatorNthChild);
            await I.scrollTo(caseMatchesValidYesLocatorNthChild);
            await I.waitForEnabled(caseMatchesValidYesLocatorNthChild);
            await I.click(caseMatchesValidYesLocatorNthChild);
            await I.waitForElement(caseMatchesImportNoLocatorNthChild);
            await I.click(caseMatchesImportNoLocatorNthChild);
            break;
        }
        // eslint-disable-next-line no-plusplus
        i--;
    }
    await I.waitForElement(commonConfig.continueButton);
    await I.scrollTo({css: commonConfig.continueButton});
    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
    await I.wait(testConfig.CaseMatchesCompletionDelay);
};
