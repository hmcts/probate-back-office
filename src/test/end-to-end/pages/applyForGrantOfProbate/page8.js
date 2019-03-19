'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    I.waitForText(applyForGrantOfProbateConfig.page8_waitForText, testConfig.TestTimeToWaitForText);

    if (crud === 'create') {
        I.click(`#deceasedDomicileInEngWales-${applyForGrantOfProbateConfig.page8_deceasedDomicileInEngWalesYes}`);
        I.fillField('#domicilityCountry', applyForGrantOfProbateConfig.page8_domicilityCountry);
        I.click('#ukEstate > div > button:nth-child(2)');
        I.fillField('#ukEstate_0_item', applyForGrantOfProbateConfig.page8_ukEstate_0_item);
        I.fillField('#ukEstate_0_value', applyForGrantOfProbateConfig.page8_ukEstate_0_value);
        I.click(`#domicilityIHTCert-${applyForGrantOfProbateConfig.page8_domicilityIHTCertYes}`);

    }

    if (crud === 'update') {
        I.fillField('#lodgedDate-day', applyForGrantOfProbateConfig.page1_lodgedDate_day_update);
        I.fillField('#lodgedDate-month', applyForGrantOfProbateConfig.page1_lodgedDate_month_update);
        I.fillField('#lodgedDate-year', applyForGrantOfProbateConfig.page1_lodgedDate_year_update);

        I.fillField('#numberOfCodicils', applyForGrantOfProbateConfig.page1_numberOfCodicils_update);

    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
