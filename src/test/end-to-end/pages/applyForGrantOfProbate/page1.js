'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(applyForGrantOfProbateConfig.page1_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#registryLocation', applyForGrantOfProbateConfig.page1_list1_registry_location);
        I.selectOption('#applicationType', applyForGrantOfProbateConfig.page1_list2_application_type);

        I.fillField('#applicationSubmittedDate-day', applyForGrantOfProbateConfig.page1_applicationSubmittedDate_day);
        I.fillField('#applicationSubmittedDate-month', applyForGrantOfProbateConfig.page1_applicationSubmittedDate_month);
        I.fillField('#applicationSubmittedDate-year', applyForGrantOfProbateConfig.page1_applicationSubmittedDate_year);

        I.selectOption('#caseType', applyForGrantOfProbateConfig.page1_list3_case_type);

        I.fillField('#extraCopiesOfGrant', applyForGrantOfProbateConfig.page1_extraCopiesOfGrant);
        I.fillField('#outsideUKGrantCopies', applyForGrantOfProbateConfig.page1_outsideUKGrantCopies);

        I.fillField('#applicationFeePaperForm', applyForGrantOfProbateConfig.page1_applicationFee);
        I.fillField('#feeForCopiesPaperForm', applyForGrantOfProbateConfig.page1_copiesFee);
        I.fillField('#totalFeePaperForm', applyForGrantOfProbateConfig.page1_totalFee);

        I.selectOption('#paperPaymentMethod', applyForGrantOfProbateConfig.page1_list4_payment_method);

    }

    if (crud === 'update') {
        I.selectOption('#selectionList', applyForGrantOfProbateConfig.page1_list5_update_option);
        I.click(commonConfig.continueButton);

        I.fillField('#boWillMessage', applyForGrantOfProbateConfig.page1_boWillMessage);

    }

    I.click(commonConfig.continueButton);
};
