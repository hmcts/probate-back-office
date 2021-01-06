'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'update') {
        await I.waitForText(createCaveatConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.waitForEnabled({css: '#expiryDate-day'}, testConfig.TestTimeToWaitForText);

        await I.fillField('#expiryDate-day', createCaveatConfig.page4_caveatExpiryDate_day_update);
        await I.fillField('#expiryDate-month', createCaveatConfig.page4_caveatExpiryDate_month_update);
        await I.fillField('#expiryDate-year', createCaveatConfig.page4_caveatExpiryDate_year_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
