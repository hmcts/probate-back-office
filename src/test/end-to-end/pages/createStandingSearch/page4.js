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

    if (crud === 'update1') {
        createStandingSearchConfig.page4_numberOfCopies_update = '2';
        I.waitForText(createStandingSearchConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#numberOfCopies', createStandingSearchConfig.page4_numberOfCopies_update);
    }

    if (crud === 'update2') {
        createStandingSearchConfig.page4_numberOfCopies_update = '3';
        I.waitForText(createStandingSearchConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#numberOfCopies', createStandingSearchConfig.page4_numberOfCopies_update);
    }

    if (crud === 'update3') {
        createStandingSearchConfig.page4_numberOfCopies_update = '4';
        I.waitForText(createStandingSearchConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#numberOfCopies', createStandingSearchConfig.page4_numberOfCopies_update);
    }

    if (crud === 'update4') {
        createStandingSearchConfig.page4_numberOfCopies_update = '5';
        I.waitForText(createStandingSearchConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#numberOfCopies', createStandingSearchConfig.page4_numberOfCopies_update);
    }

    I.click(commonConfig.continueButton);
};
