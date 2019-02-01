'use strict';

const testConfig = require('src/test/config.js');
const createWillLodgementConfig = require('./createWillLodgementConfig.json');

module.exports = function () {

    const I = this;

    I.waitForText(createWillLodgementConfig.page1.waitForText, testConfig.TestTimeToWaitForText);
  //  I.amOnPage(createWillLodgementConfig.pageUrl);

    I.selectOption(createWillLodgementConfig.page1.lists.list1.id, createWillLodgementConfig.page1.lists.list1.text);
    I.selectOption(createWillLodgementConfig.page1.lists.list2.id, createWillLodgementConfig.page1.lists.list2.text);

    I.fillField('#lodgedDate-day',createWillLodgementConfig.page1.lodgedDate.day);
    I.fillField('#lodgedDate-month',createWillLodgementConfig.page1.lodgedDate.month);
    I.fillField('#lodgedDate-year',createWillLodgementConfig.page1.lodgedDate.year);

    I.fillField('#willDate-day',createWillLodgementConfig.page1.willDate.day);
    I.fillField('#willDate-month',createWillLodgementConfig.page1.willDate.month);
    I.fillField('#willDate-year',createWillLodgementConfig.page1.willDate.year);

    I.fillField('#codicilDate-day',createWillLodgementConfig.page1.codicils.codicilDate.day);
    I.fillField('#codicilDate-month',createWillLodgementConfig.page1.codicils.codicilDate.month);
    I.fillField('#codicilDate-year',createWillLodgementConfig.page1.codicils.codicilDate.year);

    I.fillField('#numberOfCodicils', createWillLodgementConfig.page1.codicils.numberOfCodicils);

    I.click(`#jointWill-${createWillLodgementConfig.page1.jointWill}`);

    //I.waitForEnabled(createWillLodgementConfig.page1.locator, testConfig.TestTimeToWaitForText);

    I.click(createWillLodgementConfig.common.locator);
};
