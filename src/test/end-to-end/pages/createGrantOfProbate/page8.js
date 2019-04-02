'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createGrantOfProbateConfig.page8_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#deceasedDomicileInEngWales-${createGrantOfProbateConfig.page8_deceasedDomicileInEngWalesYes}`);
        I.fillField('#domicilityCountry', createGrantOfProbateConfig.page8_domicilityCountry);
        I.click('#ukEstate > div > button:nth-child(2)');
        I.fillField('#ukEstate_0_item', createGrantOfProbateConfig.page8_ukEstate_0_item);
        I.fillField('#ukEstate_0_value', createGrantOfProbateConfig.page8_ukEstate_0_value);
        I.click(`#domicilityIHTCert-${createGrantOfProbateConfig.page8_domicilityIHTCertYes}`);

    }

    if (crud === 'update') {
        I.waitForText(createGrantOfProbateConfig.page8_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#selectionList', createGrantOfProbateConfig.page8_list1_update_option);
        I.click(commonConfig.continueButton);

        I.click(`#deceasedDomicileInEngWales-${createGrantOfProbateConfig.page8_deceasedDomicileInEngWalesNo}`);

    }

    I.click(commonConfig.continueButton);
};
