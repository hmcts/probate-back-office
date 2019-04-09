'use strict';

const testConfig = require('src/test/config');
const createStandingSearchConfig = require('./createStandingSearchConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createStandingSearchConfig.page1_waitForText, testConfig.TestTimeToWaitForText);

        I.selectOption('#applicationType', createStandingSearchConfig.page1_list1_application_type);
        I.selectOption('#registryLocation', createStandingSearchConfig.page1_list2_registry_location);
    }

    if (crud === 'update1') {
        createStandingSearchConfig.page1_list2_registry_location_update = 'Manchester';
        I.waitForText(createStandingSearchConfig.page1_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#registryLocation', createStandingSearchConfig.page1_list2_registry_location_update);
    }

    if (crud === 'update2') {
        createStandingSearchConfig.page1_list2_registry_location_update = 'Leeds';
        I.waitForText(createStandingSearchConfig.page1_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#registryLocation', createStandingSearchConfig.page1_list2_registry_location_update);
    }

    if (crud === 'update3') {
        createStandingSearchConfig.page1_list2_registry_location_update = 'Birmingham';
        I.waitForText(createStandingSearchConfig.page1_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#registryLocation', createStandingSearchConfig.page1_list2_registry_location_update);
    }

    if (crud === 'update4') {
        createStandingSearchConfig.page1_list2_registry_location_update = 'Oxford';
        I.waitForText(createStandingSearchConfig.page1_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#registryLocation', createStandingSearchConfig.page1_list2_registry_location_update);
    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
