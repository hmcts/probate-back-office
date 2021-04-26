'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText('When did you send the IHT400 and IHT421 to HMRC?');
    await I.fillField('#solsIHT400Date-day', '10');
    await I.fillField('#solsIHT400Date-month', '10');
    await I.fillField('#solsIHT400Date-year', '2020');
    await I.waitForNavigationToComplete(commonConfig.continueButton);
    await I.waitForElement('#solsConfirmSignSOT1');
    await I.runAccessibilityTest();
    await I.fillField('#solsSOTForenames', completeApplicationConfig.page3_sol_forename);
    await I.fillField('#solsSOTSurname', completeApplicationConfig.page3_sol_surname);
    await I.fillField('#solsSOTJobTitle', completeApplicationConfig.page3_sol_jobtitle);

    await I.waitForNavigationToComplete(commonConfig.continueButton);

};
