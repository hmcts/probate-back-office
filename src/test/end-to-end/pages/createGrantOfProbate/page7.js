'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page7_waitForText, testConfig.WaitForTextTimeout);
        await I.click(`#applyingAsAnAttorney_${createGrantOfProbateConfig.page7_applyingAsAttorneyYes}`);
        await I.click('#attorneyOnBehalfOfNameAndAddress > div > button');
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
        }

        await I.waitForVisible({css: '#attorneyOnBehalfOfNameAndAddress_0_name'}, createGrantOfProbateConfig.page7_representativeOfName);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', createGrantOfProbateConfig.page7_representativeOfName);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayShort);
        }

        await I.click(createGrantOfProbateConfig.UKpostcodeLink);
        await I.waitForEnabled({css: '#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine1'});
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address__detailAddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address__detailAddressLine2', createGrantOfProbateConfig.address_line2);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayShort);
        }

        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address__detailAddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address__detailPostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address__detailCounty', createGrantOfProbateConfig.address_county);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address__detailPostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address__detailCountry', createGrantOfProbateConfig.address_country);

        await I.click(`#mentalCapacity_${createGrantOfProbateConfig.page7_mentalCapacityYes}`);
        await I.click(`#courtOfProtection_${createGrantOfProbateConfig.page7_courtOfProtectionYes}`);
        await I.click(`#epaOrLpa_${createGrantOfProbateConfig.page7_epaOrLpaYes}`);
        await I.click(`#epaRegistered_${createGrantOfProbateConfig.page7_epaRegisteredYes}`);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page7_amend_waitForText, testConfig.WaitForTextTimeout);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page7_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', createGrantOfProbateConfig.page7_representativeOfName_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
