'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createGrantOfProbateConfig.page9_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#ihtFormCompletedOnline-${createGrantOfProbateConfig.page9_ihtFormCompletedOnlineYes}`);
        I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page9_ihtReferenceNumber);
        I.fillField('#ihtGrossValue', createGrantOfProbateConfig.page9_ihtGrossValue);
        I.fillField('#ihtNetValue', createGrantOfProbateConfig.page9_ihtNetValue);

    }

    if (crud === 'update') {
        I.waitForText(createGrantOfProbateConfig.page9_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#selectionList', createGrantOfProbateConfig.page9_list1_update_option);
        I.waitForNavigationToComplete(commonConfig.continueButton);

        I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page9_ihtReferenceNumber_update);
    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
