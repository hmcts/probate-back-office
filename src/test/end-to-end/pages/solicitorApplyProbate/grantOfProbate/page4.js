'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (isSolicitorApplying = false) {
    const I = this;
    await I.waitForElement('#otherExecutorExists');
    await I.runAccessibilityTest();

    if (isSolicitorApplying) {
        await I.click(`#otherExecutorExists_${grantOfProbateConfig.page4_otherExecutorExists}`);

        await I.waitForText(grantOfProbateConfig.page2_waitForAdditionalExecutor, testConfig.WaitForTextTimeout);

        await I.waitForText(grantOfProbateConfig.page4_previouslyIdentifiedApplyingExecutors, testConfig.WaitForTextTimeout);
        await I.waitForText(grantOfProbateConfig.page4_previouslyIdentifiedNotApplyingExecutors, testConfig.WaitForTextTimeout);

        await I.click('#solsAdditionalExecutorList > div > button');

        await I.fillField('#solsAdditionalExecutorList_0_additionalExecForenames', grantOfProbateConfig.page2_executorFirstName);

        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }

        await I.fillField('#solsAdditionalExecutorList_0_additionalExecLastname', grantOfProbateConfig.page2_executorSurname);
        await I.click(`#solsAdditionalExecutorList_0_additionalExecNameOnWill_${grantOfProbateConfig.optionYes}`);
        await I.waitForVisible('#solsAdditionalExecutorList_0_additionalExecAliasNameOnWill', testConfig.WaitForTextTimeout);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAliasNameOnWill', grantOfProbateConfig.page2_executorAliasName);

        await I.click({css: '#solsAdditionalExecutorList_0_additionalApplying_Yes'});

        await I.waitForElement('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_postcodeInput', testConfig.WaitForTextTimeout);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_postcodeInput', grantOfProbateConfig.page2_executorPostcode);
        await I.click('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress > div  > div > button');

        await I.waitForElement('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList', testConfig.WaitForTextTimeout);
        const optLocator = {css: '#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList > option:first-child'};
        await I.waitForElement(optLocator, testConfig.WaitForTextTimeout);
        const optText = await I.grabTextFrom(optLocator);
        if (optText.indexOf(grantOfProbateConfig.noAddressFound) >= 0) {
            const addExecAddrLocator = {css: grantOfProbateConfig.page4_postcodeLink};
            await I.waitForElement(addExecAddrLocator);
            await I.waitForClickable(addExecAddrLocator);
            await I.click(addExecAddrLocator);
            await I.waitForVisible({css: '#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine1'});
            await I.fillField({css: '#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine1'}, grantOfProbateConfig.page2_executorAddress_line1);
            await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine2', grantOfProbateConfig.page2_executorAddress_line2);
            await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine3', grantOfProbateConfig.page2_executorAddress_line3);
            await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailPostTown', grantOfProbateConfig.page2_executorAddress_town);
            await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailPostCode', grantOfProbateConfig.page2_executorPostcode);
            await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailCountry', grantOfProbateConfig.page2_executorAddress_country);
        } else {
            await I.retry(10).selectOption('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList', grantOfProbateConfig.page2_executorAddress);
        }

    } else {

        await I.click(`#otherExecutorExists_${grantOfProbateConfig.optionNo}`);

    }

    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
