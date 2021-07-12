'use strict';

const testConfig = require('src/test/config');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, createGrantOfProbateConfig) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page1_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption({css: '#registryLocation'}, createGrantOfProbateConfig.page1_list1_registry_location);
        await I.selectOption({css: '#applicationType'}, createGrantOfProbateConfig.page1_list2_application_type);

        await I.fillField({css: '#applicationSubmittedDate-day'}, createGrantOfProbateConfig.page1_applicationSubmittedDate_day);
        await I.fillField({css: '#applicationSubmittedDate-month'}, createGrantOfProbateConfig.page1_applicationSubmittedDate_month);
        await I.fillField({css: '#applicationSubmittedDate-year'}, createGrantOfProbateConfig.page1_applicationSubmittedDate_year);
        await I.fillField({css: '#solsSOTForenames'}, createGrantOfProbateConfig.page1_solsSOTForenames);
        await I.fillField({css: '#solsSOTSurname'}, createGrantOfProbateConfig.page1_solsSOTSurname);
        await I.fillField({css: '#solsSolicitorFirmName'}, createGrantOfProbateConfig.page1_solsSolicitorFirmName);
        await I.click({css: `#solsSolicitorIsExec-${createGrantOfProbateConfig.page1_solsSolicitorIsExec}`});
        await I.click({css: `#solsSolicitorIsApplying-${createGrantOfProbateConfig.page1_solsSolicitorIsApplying}`});

        await I.click(createGrantOfProbateConfig.UKpostcodeLink);

        await I.fillField('#solsSolicitorAddress_AddressLine1', createGrantOfProbateConfig.page1_sols_address_line1);
        await I.fillField('#solsSolicitorAddress_AddressLine2', createGrantOfProbateConfig.page1_sols_address_line2);
        await I.fillField('#solsSolicitorAddress_PostTown', createGrantOfProbateConfig.page1_sols_address_town);
        await I.fillField('#solsSolicitorAddress_County', createGrantOfProbateConfig.page1_sols_address_county);
        await I.fillField('#solsSolicitorAddress_PostCode', createGrantOfProbateConfig.page1_sols_address_postcode);
        await I.fillField('#solsSolicitorAddress_Country', createGrantOfProbateConfig.page1_sols_address_country);

        await I.fillField('#solsSolicitorAppReference', createGrantOfProbateConfig.page1_solsSolicitorAppReference);
        await I.fillField('#solsSolicitorEmail', createGrantOfProbateConfig.page1_solsSolicitorEmail);
        await I.fillField('#solsSolicitorPhoneNumber', createGrantOfProbateConfig.page1_solsSolicitorPhoneNumber);
        await I.fillField('#solsSOTJobTitle', createGrantOfProbateConfig.page1_solsSOTJobTitle);

        await I.selectOption('#caseType', createGrantOfProbateConfig.page1_list3_case_type);
        await I.click({css: `#paperForm-${createGrantOfProbateConfig.page1_paperForm}`});

        await I.fillField('#extraCopiesOfGrant', createGrantOfProbateConfig.page1_extraCopiesOfGrant);
        await I.fillField('#outsideUKGrantCopies', createGrantOfProbateConfig.page1_outsideUKGrantCopies);

        await I.fillField('#applicationFeePaperForm', createGrantOfProbateConfig.page1_applicationFee);
        await I.fillField('#feeForCopiesPaperForm', createGrantOfProbateConfig.page1_copiesFee);
        await I.fillField('#totalFeePaperForm', createGrantOfProbateConfig.page1_totalFee);

        await I.selectOption('#paperPaymentMethod', createGrantOfProbateConfig.page1_list4_payment_method);
        await I.click({css: `#languagePreferenceWelsh-${createGrantOfProbateConfig.page1_optionNo}`});
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page1_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page1_list5_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.fillField('#boWillMessage', createGrantOfProbateConfig.page1_boWillMessage);
        await I.waitForEnabled({css: '#caseHandedOffToLegacySite-No'});
        await I.click('#caseHandedOffToLegacySite-No');
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
