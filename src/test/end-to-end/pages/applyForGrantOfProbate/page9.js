'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    I.waitForText(applyForGrantOfProbateConfig.page9_waitForText, testConfig.TestTimeToWaitForText);

    if (crud === 'create') {
        I.click(`#ihtFormCompletedOnline-${applyForGrantOfProbateConfig.page9_ihtFormCompletedOnlineYes}`);
        I.fillField('#ihtReferenceNumber', applyForGrantOfProbateConfig.page9_ihtReferenceNumber);
        I.fillField('#ihtGrossValue', applyForGrantOfProbateConfig.page9_ihtGrossValue);
        I.fillField('#ihtNetValue', applyForGrantOfProbateConfig.page9_ihtNetValue);

    }

    if (crud === 'update') {
        I.fillField('#lodgedDate-day', applyForGrantOfProbateConfig.page1_lodgedDate_day_update);
        I.fillField('#lodgedDate-month', applyForGrantOfProbateConfig.page1_lodgedDate_month_update);
        I.fillField('#lodgedDate-year', applyForGrantOfProbateConfig.page1_lodgedDate_year_update);

        I.fillField('#numberOfCodicils', applyForGrantOfProbateConfig.page1_numberOfCodicils_update);

    }

    I.click(commonConfig.continueButton);
};
