'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page8_waitForText, testConfig.TestTimeToWaitForText);
        await I.click(`#deceasedDomicileInEngWales-${createGrantOfProbateConfig.page8_deceasedDomicileInEngWalesYes}`);
        await I.fillField('#domicilityCountry', createGrantOfProbateConfig.page8_domicilityCountry);
        await I.click('#ukEstate > div > button:nth-child(2)');
        await I.fillField('#ukEstate_0_item', createGrantOfProbateConfig.page8_ukEstate_0_item);
        await I.fillField('#ukEstate_0_value', createGrantOfProbateConfig.page8_ukEstate_0_value);
        await I.click(`#domicilityIHTCert-${createGrantOfProbateConfig.page8_domicilityIHTCertYes}`);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page8_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page8_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        const locator = {css: `#deceasedDomicileInEngWales-${createGrantOfProbateConfig.page8_deceasedDomicileInEngWalesNo}`};
        await I.waitForElement(locator);
        await I.click(locator);
        await I.fillField({css: '#domicilityCountry'}, createGrantOfProbateConfig.page8_domicilityCountry);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
