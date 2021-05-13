'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page8_waitForText, testConfig.TestTimeToWaitForText);
        await I.click(`#applyingAsAnAttorney-${createGrantOfProbateConfig.page8_applyingAsAttorneyYes}`);
        await I.click('#attorneyOnBehalfOfNameAndAddress > div > button');
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(testConfig.ManualDelayMedium); // needed in order to be able to switch off auto delay for local dev
        }

        await I.waitForVisible({css: '#attorneyOnBehalfOfNameAndAddress_0_name'}, createGrantOfProbateConfig.page8_representativeOfName);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', createGrantOfProbateConfig.page8_representativeOfName);

        await I.click(createGrantOfProbateConfig.UKpostcodeLink);
        await I.waitForEnabled({css: '#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine1'});
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_PostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_County', createGrantOfProbateConfig.address_county);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_PostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_Country', createGrantOfProbateConfig.address_country);

        await I.click(`#mentalCapacity-${createGrantOfProbateConfig.page8_mentalCapacityYes}`);
        await I.click(`#courtOfProtection-${createGrantOfProbateConfig.page8_courtOfProtectionYes}`);
        await I.click(`#epaOrLpa-${createGrantOfProbateConfig.page8_epaOrLpaYes}`);
        await I.click(`#epaRegistered-${createGrantOfProbateConfig.page8_epaRegisteredYes}`);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page8_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page8_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', createGrantOfProbateConfig.page8_representativeOfName_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
