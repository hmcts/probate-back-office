'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig = require('./createWillLodgementConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    I.waitForText(createWillLodgementConfig.page1_waitForText, testConfig.TestTimeToWaitForText);

    if (crud === 'create') {
        I.selectOption('#registryLocation', createWillLodgementConfig.page1_list1_registry_location);
        I.selectOption('#lodgementType', createWillLodgementConfig.page1_list2_lodgement_type);

        I.fillField('#lodgedDate-day', createWillLodgementConfig.page1_lodgedDate_day);
        I.fillField('#lodgedDate-month', createWillLodgementConfig.page1_lodgedDate_month);
        I.fillField('#lodgedDate-year', createWillLodgementConfig.page1_lodgedDate_year);

        I.fillField('#willDate-day', createWillLodgementConfig.page1_willDate_day);
        I.fillField('#willDate-month', createWillLodgementConfig.page1_willDate_month);
        I.fillField('#willDate-year', createWillLodgementConfig.page1_willDate_year);

        I.fillField('#codicilDate-day', createWillLodgementConfig.page1_codicilDate_day);
        I.fillField('#codicilDate-month', createWillLodgementConfig.page1_codicilDate_month);
        I.fillField('#codicilDate-year', createWillLodgementConfig.page1_codicilDate_year);

        I.fillField('#numberOfCodicils', createWillLodgementConfig.page1_numberOfCodicils);

        I.click(`#jointWill-${createWillLodgementConfig.page1_jointWill}`);
    }

    if (crud === 'update') {
        I.fillField('#lodgedDate-day', createWillLodgementConfig.page1_lodgedDate_day_update);
        I.fillField('#lodgedDate-month', createWillLodgementConfig.page1_lodgedDate_month_update);
        I.fillField('#lodgedDate-year', createWillLodgementConfig.page1_lodgedDate_year_update);

        I.fillField('#numberOfCodicils', createWillLodgementConfig.page1_numberOfCodicils_update);

    }

    I.click(commonConfig.continueButton);
};
