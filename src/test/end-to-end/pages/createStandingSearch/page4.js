'use strict';

const testConfig = require('src/test/config');
const createStandingSearchConfig = require('./createStandingSearchConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createStandingSearchConfig.page4_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#numberOfCopies', createStandingSearchConfig.page4_numberOfCopies);

    }

    if (crud === 'update') {
        I.waitForText(createStandingSearchConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#numberOfCopies', createStandingSearchConfig.page4_numberOfCopies_update);
    }

    I.click(commonConfig.continueButton);
};
