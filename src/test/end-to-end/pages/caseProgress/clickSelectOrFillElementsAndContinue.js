'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function (elementLocators) {
    const I = this;

    /* eslint-disable no-await-in-loop */
    for (let i=0; i < elementLocators.length; i++) {
        const itm = elementLocators[i];
        await I.wait(testConfig.CaseProgressClickSelectFillDelay);
        await I.waitForVisible(itm.locator);
        await I.waitForEnabled(itm.locator);
        if (itm.text) {
            await I.fillField(itm.locator, itm.text);
        } else if (itm.option) {
            await I.selectOption(itm.locator, itm.option);
        } else {
            await I.click(itm.locator);
        }
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
