'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(applyForGrantOfProbateConfig.page8_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#deceasedDomicileInEngWales-${applyForGrantOfProbateConfig.page8_deceasedDomicileInEngWalesYes}`);
        I.fillField('#domicilityCountry', applyForGrantOfProbateConfig.page8_domicilityCountry);
        I.click('#ukEstate > div > button:nth-child(2)');
        I.fillField('#ukEstate_0_item', applyForGrantOfProbateConfig.page8_ukEstate_0_item);
        I.fillField('#ukEstate_0_value', applyForGrantOfProbateConfig.page8_ukEstate_0_value);
        I.click(`#domicilityIHTCert-${applyForGrantOfProbateConfig.page8_domicilityIHTCertYes}`);

    }

    if (crud === 'update') {
        I.selectOption('#selectionList', applyForGrantOfProbateConfig.page8_list1_update_option);
        I.click(commonConfig.continueButton);

        I.click(`#deceasedDomicileInEngWales-${applyForGrantOfProbateConfig.page8_deceasedDomicileInEngWalesNo}`);

    }

    I.click(commonConfig.continueButton);
};
