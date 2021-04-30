'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;

    await I.waitForText('When did you send the IHT400 and IHT421 to HMRC?');
    await I.runAccessibilityTest();
    await I.fillField('#solsIHT400Date-day', '10');
    await I.fillField('#solsIHT400Date-month', '10');
    await I.fillField('#solsIHT400Date-year', '2020');
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
