'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page8_waitForText, testConfig.TestTimeToWaitForText);
        await I.click(`#deceasedDomicileInEngWales-Yes`);
        await I.click('#ukEstate > div > button:nth-child(2)');
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(0.25);
        }
        await I.fillField('#ukEstate_0_item', createGrantOfProbateConfig.page8_ukEstate_0_item);
        await I.fillField('#ukEstate_0_value', createGrantOfProbateConfig.page8_ukEstate_0_value);
        await I.click(`#domicilityIHTCert-${createGrantOfProbateConfig.page8_domicilityIHTCertYes}`);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page8_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page8_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        createGrantOfProbateConfig.page8_deceasedDomicileInEngWales = 'No';
        const locator = {css: `#deceasedDomicileInEngWales-No`};
        await I.waitForElement(locator);
        await I.click(locator);
        await I.fillField({css: '#domicilityCountry'}, createGrantOfProbateConfig.page8_domicilityCountry);
    }

    // occasionally the last input is not recorded when auto delay off
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.5);
    }

    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
