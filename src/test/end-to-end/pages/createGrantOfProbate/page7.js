'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page7_waitForText, testConfig.TestTimeToWaitForText);
        await I.click(`#applyingAsAnAttorney-${createGrantOfProbateConfig.page7_applyingAsAttorneyYes}`);
        await I.click('#attorneyOnBehalfOfNameAndAddress > div > button');
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', createGrantOfProbateConfig.page7_representativeOfName);

        await I.click(createGrantOfProbateConfig.UKpostcodeLink);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_PostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_County', createGrantOfProbateConfig.address_county);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_PostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_Country', createGrantOfProbateConfig.address_country);

        await I.click(`#mentalCapacity-${createGrantOfProbateConfig.page7_mentalCapacityYes}`);
        await I.click(`#courtOfProtection-${createGrantOfProbateConfig.page7_courtOfProtectionYes}`);
        await I.click(`#epaOrLpa-${createGrantOfProbateConfig.page7_epaOrLpaYes}`);
        await I.click(`#epaRegistered-${createGrantOfProbateConfig.page7_epaRegisteredYes}`);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page7_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page7_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', createGrantOfProbateConfig.page7_representativeOfName_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
