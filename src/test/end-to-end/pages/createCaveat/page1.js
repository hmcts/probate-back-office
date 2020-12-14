'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createCaveatConfig.page1_waitForText, testConfig.TestTimeToWaitForText);

        await I.selectOption('#applicationType', createCaveatConfig.page1_list1_application_type);
        await I.selectOption('#registryLocation', createCaveatConfig.page1_list2_registry_location);
    }

    if (crud === 'update') {
        await I.waitForText(createCaveatConfig.page1_amend_waitForText, testConfig.TestTimeToWaitForText);

        await I.selectOption('#registryLocation', createCaveatConfig.page1_list2_registry_location_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
