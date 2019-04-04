'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createGrantOfProbateConfig.page1_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#registryLocation', createGrantOfProbateConfig.page1_list1_registry_location);
        I.selectOption('#applicationType', createGrantOfProbateConfig.page1_list2_application_type);

        I.fillField('#applicationSubmittedDate-day', createGrantOfProbateConfig.page1_applicationSubmittedDate_day);
        I.fillField('#applicationSubmittedDate-month', createGrantOfProbateConfig.page1_applicationSubmittedDate_month);
        I.fillField('#applicationSubmittedDate-year', createGrantOfProbateConfig.page1_applicationSubmittedDate_year);

        I.selectOption('#caseType', createGrantOfProbateConfig.page1_list3_case_type);

        I.fillField('#extraCopiesOfGrant', createGrantOfProbateConfig.page1_extraCopiesOfGrant);
        I.fillField('#outsideUKGrantCopies', createGrantOfProbateConfig.page1_outsideUKGrantCopies);

        I.fillField('#applicationFeePaperForm', createGrantOfProbateConfig.page1_applicationFee);
        I.fillField('#feeForCopiesPaperForm', createGrantOfProbateConfig.page1_copiesFee);
        I.fillField('#totalFeePaperForm', createGrantOfProbateConfig.page1_totalFee);

        I.selectOption('#paperPaymentMethod', createGrantOfProbateConfig.page1_list4_payment_method);

    }

    if (crud === 'update') {
        I.waitForText(createGrantOfProbateConfig.page1_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#selectionList', createGrantOfProbateConfig.page1_list5_update_option);
        I.click(commonConfig.continueButton);
        I.seeElement('#boWillMessage');
        I.fillField('#boWillMessage', createGrantOfProbateConfig.page1_boWillMessage);

    }

    I.click(commonConfig.continueButton);
};
