'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (verifyTrustCorpOpts) {
    const I = this;
    await I.waitForClickable({css: `#dispenseWithNotice-${grantOfProbateConfig.optionYes}`});
    await I.runAccessibilityTest();
    await I.click(`#dispenseWithNotice-${grantOfProbateConfig.optionYes}`);
    if (verifyTrustCorpOpts) {
        await I.verifyTitleAndClearingTypeOptions();
    } else {
        await I.scrollTo('#titleAndClearingType-TCTNoT');
    }

    await I.waitForClickable({css: '#titleAndClearingType-TCTNoT'});
    await I.click({css: '#titleAndClearingType-TCTNoT'});
    await I.fillField('#titleAndClearingTypeNoT', grantOfProbateConfig.page2_titleAndClearingTypeNoT);

    await I.waitForClickable({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    await I.waitForElement('#trustCorpName');
    await I.fillField('#trustCorpName', grantOfProbateConfig.page2_nameOfTrustCorp);

    await I.waitForText(grantOfProbateConfig.page2_waitForAdditionPerson, testConfig.TestTimeToWaitForText);
    await I.click('#additionalExecutorsTrustCorpList > div > button');
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecForenames', grantOfProbateConfig.page2_executorFirstName);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.25);
    }

    await I.fillField('#primaryApplicantForenames', grantOfProbateConfig.page2_primaryApplicantFirstName);
    await I.fillField('#primaryApplicantSurname', grantOfProbateConfig.page2_primaryApplicantSurname);
    await I.click(`#primaryApplicantHasAlias-${grantOfProbateConfig.optionYes}`);
    await I.fillField('#solsExecutorAliasNames', grantOfProbateConfig.page2_primaryApplicantWillName);
    await I.click(`#primaryApplicantIsApplying-${grantOfProbateConfig.optionYes}`);

    await I.click(grantOfProbateConfig.UKpostcodeLink);
    await I.fillField('#primaryApplicantAddress_AddressLine1', grantOfProbateConfig.address_line1);
    await I.fillField('#primaryApplicantAddress_AddressLine2', grantOfProbateConfig.address_line2);
    await I.fillField('#primaryApplicantAddress_AddressLine3', grantOfProbateConfig.address_line3);
    await I.fillField('#primaryApplicantAddress_PostTown', grantOfProbateConfig.address_town);
    await I.fillField('#primaryApplicantAddress_County', grantOfProbateConfig.address_county);
    await I.fillField('#primaryApplicantAddress_PostCode', grantOfProbateConfig.address_postcode);
    await I.fillField('#primaryApplicantAddress_Country', grantOfProbateConfig.address_country);

    await I.click(`#otherExecutorExists-${grantOfProbateConfig.optionYes}`);

    await I.waitForText(grantOfProbateConfig.page2_waitForAdditionalExecutor, testConfig.TestTimeToWaitForText);

    await I.click('#solsAdditionalExecutorList > div > button');
    await I.fillField('#solsAdditionalExecutorList_0_additionalExecForenames', grantOfProbateConfig.page2_executorFirstName);
    if (!testConfig.TestAutoDelayEnabled) {
        // only valid for local dev where we need it to run as fast as poss to minimise
        // lost dev time
        await I.wait(0.25);
    }
    await I.fillField('#solsAdditionalExecutorList_0_additionalExecLastname', grantOfProbateConfig.page2_executorSurname);
    await I.click(`#solsAdditionalExecutorList_0_additionalExecNameOnWill-${grantOfProbateConfig.optionYes}`);
    await I.click(`#solsAdditionalExecutorList_0_additionalApplying-${grantOfProbateConfig.optionYes}`);

    await I.waitForElement('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_postcodeInput', testConfig.TestTimeToWaitForText);
    await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_postcodeInput', grantOfProbateConfig.page2_executorPostcode);
    await I.click('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress > div  > div > button');
    await I.waitForElement('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList', testConfig.TestTimeToWaitForText);
    await I.fillField('#solsAdditionalExecutorList_0_additionalExecAliasNameOnWill', grantOfProbateConfig.page2_executorAliasName);
    const optLocator = {css: '#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList > option:first-child'};
    await I.waitForElement(optLocator, testConfig.TestTimeToWaitForText);
    const optText = await I.grabTextFrom(optLocator);
    if (optText.indexOf(grantOfProbateConfig.noAddressFound) >= 0) {
        const addExecAddrLocator = {css: grantOfProbateConfig.UKpostcodeLink};
        await I.waitForVisible(addExecAddrLocator);
        await I.click(addExecAddrLocator);
        await I.waitForVisible({css: '#solsAdditionalExecutorList_0_additionalExecAddress_AddressLine1'});
        await I.fillField({css: '#solsAdditionalExecutorList_0_additionalExecAddress_AddressLine1'}, grantOfProbateConfig.page2_executorAddress_line1);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress_AddressLine2', grantOfProbateConfig.page2_executorAddress_line2);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress_AddressLine3', grantOfProbateConfig.page2_executorAddress_line3);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress_PostTown', grantOfProbateConfig.page2_executorAddress_town);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress_PostCode', grantOfProbateConfig.page2_executorPostcode);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress_Country', grantOfProbateConfig.page2_executorAddress_country);
    } else {
        await I.retry(10).selectOption('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList', grantOfProbateConfig.page2_executorAddress);
    }

    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecLastname', grantOfProbateConfig.page2_executorSurname);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecutorTrustCorpPosition', grantOfProbateConfig.page2_positionInTrustCorp);
    await I.click(grantOfProbateConfig.UKpostcodeLink);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecAddress_AddressLine1', grantOfProbateConfig.address_line1);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecAddress_AddressLine2', grantOfProbateConfig.address_line2);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecAddress_AddressLine3', grantOfProbateConfig.address_line3);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecAddress_PostTown', grantOfProbateConfig.address_town);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecAddress_County', grantOfProbateConfig.address_county);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecAddress_PostCode', grantOfProbateConfig.address_postcode);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecAddress_Country', grantOfProbateConfig.address_country);

    await I.fillField('#lodgementAddress', grantOfProbateConfig.page2_lodgementAddress);
    await I.fillField('#lodgementDate-day', grantOfProbateConfig.page2_lodgementDate_day);
    await I.fillField('#lodgementDate-month', grantOfProbateConfig.page2_lodgementDate_month);
    await I.fillField('#lodgementDate-year', grantOfProbateConfig.page2_lodgementDate_year);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
