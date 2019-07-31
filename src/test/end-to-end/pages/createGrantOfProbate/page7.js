'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createGrantOfProbateConfig.page7_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#applyingAsAnAttorney-${createGrantOfProbateConfig.page7_applyingAsAttorneyYes}`);
        I.click('#attorneyOnBehalfOfNameAndAddress > div > button');
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', createGrantOfProbateConfig.page7_representativeOfName);

        I.click(createGrantOfProbateConfig.UKpostcodeLink);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine1', createGrantOfProbateConfig.address_line1);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine2', createGrantOfProbateConfig.address_line2);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine3', createGrantOfProbateConfig.address_line3);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_PostTown', createGrantOfProbateConfig.address_town);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_County', createGrantOfProbateConfig.address_county);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_PostCode', createGrantOfProbateConfig.address_postcode);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_Country', createGrantOfProbateConfig.address_country);

        I.click(`#mentalCapacity-${createGrantOfProbateConfig.page7_mentalCapacityYes}`);
        I.click(`#courtOfProtection-${createGrantOfProbateConfig.page7_courtOfProtectionYes}`);
        I.click(`#epaOrLpa-${createGrantOfProbateConfig.page7_epaOrLpaYes}`);
        I.click(`#epaRegistered-${createGrantOfProbateConfig.page7_epaRegisteredYes}`);
    }

    if (crud === 'update') {
        I.waitForText(createGrantOfProbateConfig.page7_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#selectionList', createGrantOfProbateConfig.page7_list1_update_option);
        I.waitForNavigationToComplete(commonConfig.continueButton);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', createGrantOfProbateConfig.page7_representativeOfName_update);

    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
