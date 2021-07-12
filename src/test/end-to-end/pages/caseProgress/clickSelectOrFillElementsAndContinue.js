'use strict';
const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (elementLocators) {
    const I = this;

    /* eslint-disable no-await-in-loop */
    for (let i=0; i < elementLocators.length; i++) {
        const itm = elementLocators[i];
        // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
        if (testConfig.TestForXUI) {
            await I.wait(3);
        }
        await I.waitForVisible(itm.locator);
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
