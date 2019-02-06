'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig2 = require('./createWillLodgementFastConfig2');

module.exports = function () {

    const I = this;

    I.waitForText(createWillLodgementConfig2.page1_waitForText, testConfig.TestTimeToWaitForText);
    //  I.amOnPage(createWillLodgementConfig2.pageUrl);

    I.selectOption('#registryLocation', createWillLodgementConfig2.page1_list1_registry_location);
    I.selectOption('#lodgementType', createWillLodgementConfig2.page1_list2_lodgement_type);

    I.fillField('#lodgedDate-day', createWillLodgementConfig2.page1_lodgedDate_day);
    I.fillField('#lodgedDate-month', createWillLodgementConfig2.page1_lodgedDate_month);
    I.fillField('#lodgedDate-year', createWillLodgementConfig2.page1_lodgedDate_year);

    I.fillField('#willDate-day', createWillLodgementConfig2.page1_willDate_day);
    I.fillField('#willDate-month', createWillLodgementConfig2.page1_willDate_month);
    I.fillField('#willDate-year', createWillLodgementConfig2.page1_willDate_year);

    I.fillField('#codicilDate-day', createWillLodgementConfig2.page1_codicilDate_day);
    I.fillField('#codicilDate-month', createWillLodgementConfig2.page1_codicilDate_month);
    I.fillField('#codicilDate-year', createWillLodgementConfig2.page1_codicilDate_year);

    I.fillField('#numberOfCodicils', createWillLodgementConfig2.page1_numberOfCodicils);

    I.click(`#jointWill-${createWillLodgementConfig2.page1_jointWill}`);

    //I.waitForEnabled(createWillLodgementConfig2.page1.locator, testConfig.TestTimeToWaitForText);

    I.click(createWillLodgementConfig2.continueButton);
};
