'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    I.waitForText(createCaveatConfig.page1_waitForText, testConfig.TestTimeToWaitForText);

    if (crud === 'create') {
        I.selectOption('#applicationType', createCaveatConfig.page1_list1_application_type);
        I.selectOption('#registryLocation', createCaveatConfig.page1_list2_registry_location);
    }

    if (crud === 'update') {
        I.selectOption('#registryLocation', createCaveatConfig.page1_list2_registry_location_update);
    }

    I.click(commonConfig.continueButton);
};
