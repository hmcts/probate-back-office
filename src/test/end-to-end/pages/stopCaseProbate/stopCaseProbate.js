'use strict';

const testConfig = require('src/test/config.cjs');

module.exports = async function () {

    const I = this;
    await I.waitForEnabled({xpath: '//button[normalize-space()="Add new"]'}, testConfig.WaitForTextTimeout || 60);
    await I.click('//button[@class="button"][normalize-space()="Add new"]');
    await I.selectOption('//select[@id="boCaseStopReasonList_0_caseStopReason"]', 'IHT forms not received');
    await I.wait(testConfig.CaseworkerGoButtonClickDelay);
    await I.click('//button[normalize-space()="Continue"]');

};
