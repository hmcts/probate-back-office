'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page9_waitForText, testConfig.WaitForTextTimeout);
        await I.click(`#deceasedDomicileInEngWales_${createGrantOfProbateConfig.page9_deceasedDomicileInEngWalesYes}`);
        await I.fillField('#domicilityCountry', createGrantOfProbateConfig.page9_domicilityCountry);
        await I.click('#ukEstate > div > button:nth-child(2)');
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.fillField('#ukEstate_0_item', createGrantOfProbateConfig.page9_ukEstate_0_item);
        await I.fillField('#ukEstate_0_value', createGrantOfProbateConfig.page9_ukEstate_0_value);
        const ihtLocator = {css: `#domicilityIHTCert_${createGrantOfProbateConfig.page9_domicilityIHTCertYes}`};
        await I.waitForEnabled(ihtLocator);
        await I.click(ihtLocator);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page9_amend_waitForText, testConfig.WaitForTextTimeout);
        await I.waitForEnabled('#selectionList');
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page9_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        const locator = {css: `#deceasedDomicileInEngWales_${createGrantOfProbateConfig.page9_deceasedDomicileInEngWalesNo}`};
        await I.waitForEnabled(locator);
        await I.click(locator);
        await I.fillField({css: '#domicilityCountry'}, createGrantOfProbateConfig.page9_domicilityCountry);
    }

    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayMedium);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
