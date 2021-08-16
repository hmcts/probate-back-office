'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (elementLocators, delayPerLocator = 0) {
    const I = this;

    /* eslint-disable no-await-in-loop */
    for (let i=0; i < elementLocators.length; i++) {
        await I.wait(delayPerLocator);
        await I.waitForElement(elementLocators[i]);
        await I.waitForClickable(elementLocators[i]);
        await I.click(elementLocators[i]);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
