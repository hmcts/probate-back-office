'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig = require('./createWillLodgementConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createWillLodgementConfig.page1_waitForText, testConfig.TestTimeToWaitForText);

        await I.selectOption('#applicationType', createWillLodgementConfig.page1_list1_application_type);
        await I.selectOption('#registryLocation', createWillLodgementConfig.page1_list2_registry_location);
        await I.selectOption('#lodgementType', createWillLodgementConfig.page1_list3_lodgement_type);

        await I.fillField('#lodgedDate-day', createWillLodgementConfig.page1_lodgedDate_day);
        await I.fillField('#lodgedDate-month', createWillLodgementConfig.page1_lodgedDate_month);
        await I.fillField('#lodgedDate-year', createWillLodgementConfig.page1_lodgedDate_year);

        await I.fillField('#willDate-day', createWillLodgementConfig.page1_willDate_day);
        await I.fillField('#willDate-month', createWillLodgementConfig.page1_willDate_month);
        await I.fillField('#willDate-year', createWillLodgementConfig.page1_willDate_year);

        await I.fillField('#codicilDate-day', createWillLodgementConfig.page1_codicilDate_day);
        await I.fillField('#codicilDate-month', createWillLodgementConfig.page1_codicilDate_month);
        await I.fillField('#codicilDate-year', createWillLodgementConfig.page1_codicilDate_year);

        await I.fillField('#numberOfCodicils', createWillLodgementConfig.page1_numberOfCodicils);

        await I.click(`#jointWill-${createWillLodgementConfig.page1_jointWill}`);
    }

    if (crud === 'update') {
        await I.waitForText(createWillLodgementConfig.page1_amend_waitForText, testConfig.TestTimeToWaitForText);

        await I.selectOption('#registryLocation', createWillLodgementConfig.page1_list2_registry_location_update);
        await I.selectOption('#lodgementType', createWillLodgementConfig.page1_list3_lodgement_type_update);

        await I.fillField('#lodgedDate-day', createWillLodgementConfig.page1_lodgedDate_day_update);
        await I.fillField('#lodgedDate-month', createWillLodgementConfig.page1_lodgedDate_month_update);
        await I.fillField('#lodgedDate-year', createWillLodgementConfig.page1_lodgedDate_year_update);

        await I.fillField('#numberOfCodicils', createWillLodgementConfig.page1_numberOfCodicils_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
