'use strict';

const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsSOTForenames');
    await I.fillField('#solsSOTForenames', completeApplicationConfig.page3_sol_forename);
    await I.fillField('#solsSOTSurname', completeApplicationConfig.page3_sol_surname);
    await I.fillField('#solsSOTJobTitle', completeApplicationConfig.page3_sol_jobtitle);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
