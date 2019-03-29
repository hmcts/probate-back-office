'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(applyForGrantOfProbateConfig.page9_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#ihtFormCompletedOnline-${applyForGrantOfProbateConfig.page9_ihtFormCompletedOnlineYes}`);
        I.fillField('#ihtReferenceNumber', applyForGrantOfProbateConfig.page9_ihtReferenceNumber);
        I.fillField('#ihtGrossValue', applyForGrantOfProbateConfig.page9_ihtGrossValue);
        I.fillField('#ihtNetValue', applyForGrantOfProbateConfig.page9_ihtNetValue);

    }

    if (crud === 'update') {
        I.selectOption('#selectionList', applyForGrantOfProbateConfig.page9_list1_update_option);
        I.click(commonConfig.continueButton);

        I.fillField('#ihtReferenceNumber', applyForGrantOfProbateConfig.page9_ihtReferenceNumber_update);
    }

    I.click(commonConfig.continueButton);
};
