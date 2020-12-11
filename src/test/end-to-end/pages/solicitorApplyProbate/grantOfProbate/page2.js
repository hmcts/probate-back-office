'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (isSolcitorMainApplicant = false) {
    const I = this;
    await I.waitForElement('#otherExecutorExists');

    if (isSolcitorMainApplicant) {
        await I.click(`#otherExecutorExists-${grantOfProbateConfig.optionNo}`);
    } else {
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
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecLastname', grantOfProbateConfig.page2_executorSurname);
        await I.click(`#solsAdditionalExecutorList_0_additionalExecNameOnWill-${grantOfProbateConfig.optionYes}`);
        await I.click(`#solsAdditionalExecutorList_0_additionalApplying-${grantOfProbateConfig.optionYes}`);

        await I.waitForElement('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_postcodeInput', testConfig.TestTimeToWaitForText);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_postcodeInput', grantOfProbateConfig.page2_executorPostcode);
        await I.click('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress > div  > div > button');
        await I.waitForElement('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList', testConfig.TestTimeToWaitForText);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAliasNameOnWill', grantOfProbateConfig.page2_executorAliasName);
        await I.retry(10).selectOption('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList', grantOfProbateConfig.page2_executorAddress);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
