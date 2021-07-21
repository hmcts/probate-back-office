'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, forAmendOfSolicitorCreatedCase) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page1_waitForText, testConfig.WaitForTextTimeout);
        await I.selectOption('#registryLocation', createGrantOfProbateConfig.page1_list1_registry_location);
        await I.selectOption('#applicationType', createGrantOfProbateConfig.page1_list2_application_type);

        await I.fillField('#applicationSubmittedDate-day', createGrantOfProbateConfig.page1_applicationSubmittedDate_day);
        await I.fillField('#applicationSubmittedDate-month', createGrantOfProbateConfig.page1_applicationSubmittedDate_month);
        await I.fillField('#applicationSubmittedDate-year', createGrantOfProbateConfig.page1_applicationSubmittedDate_year);
        await I.click({css: `#paperForm_${createGrantOfProbateConfig.page1_optionNo}`});

        await I.selectOption('#caseType', createGrantOfProbateConfig.page1_list3_case_type);

        await I.fillField('#extraCopiesOfGrant', createGrantOfProbateConfig.page1_extraCopiesOfGrant);
        await I.fillField('#outsideUKGrantCopies', createGrantOfProbateConfig.page1_outsideUKGrantCopies);

        await I.fillField('#applicationFeePaperForm', createGrantOfProbateConfig.page1_applicationFee);
        await I.fillField('#feeForCopiesPaperForm', createGrantOfProbateConfig.page1_copiesFee);
        await I.fillField('#totalFeePaperForm', createGrantOfProbateConfig.page1_totalFee);

        await I.selectOption('#paperPaymentMethod', createGrantOfProbateConfig.page1_list4_payment_method);
        await I.click({css: `#languagePreferenceWelsh_${createGrantOfProbateConfig.page1_optionNo}`});
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page1_amend_waitForText, testConfig.WaitForTextTimeout);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page1_list5_update_option);
        if (forAmendOfSolicitorCreatedCase) {
            await I.click({css: `#languagePreferenceWelsh_${createGrantOfProbateConfig.page1_optionNo}`});
        }
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.waitForEnabled({css: '#boWillMessage'});
        await I.fillField({css: '#boWillMessage'}, createGrantOfProbateConfig.page1_boWillMessage);
        await I.waitForEnabled({css: `#caseHandedOffToLegacySite_${createGrantOfProbateConfig.page1_optionNo}`});
        await I.click({css: `#caseHandedOffToLegacySite_${createGrantOfProbateConfig.page1_optionNo}`});
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
