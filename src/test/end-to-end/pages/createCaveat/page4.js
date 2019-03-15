'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    I.waitForText(createCaveatConfig.page4_waitForText, testConfig.TestTimeToWaitForText);

    if (crud === 'update') {
        I.fillField('#expiryDate-day', createCaveatConfig.page4_caveatExpiryDate_day_update);
        I.fillField('#expiryDate-month', createCaveatConfig.page4_caveatExpiryDate_month_update);
        I.fillField('#expiryDate-year', createCaveatConfig.page4_caveatExpiryDate_year_update);
    }

    I.click(commonConfig.continueButton);
};
