'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(applyForGrantOfProbateConfig.page7_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#applyingAsAnAttorney-${applyForGrantOfProbateConfig.page7_applyingAsAttorneyYes}`);
        I.click('#attorneyOnBehalfOfNameAndAddress > div > button');
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', applyForGrantOfProbateConfig.page7_representativeOfName);

        I.click(applyForGrantOfProbateConfig.UKpostcodeLink);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine1', applyForGrantOfProbateConfig.address_line1);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine2', applyForGrantOfProbateConfig.address_line2);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_AddressLine3', applyForGrantOfProbateConfig.address_line3);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_PostTown', applyForGrantOfProbateConfig.address_town);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_County', applyForGrantOfProbateConfig.address_county);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_PostCode', applyForGrantOfProbateConfig.address_postcode);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_address_Country', applyForGrantOfProbateConfig.address_country);

        I.click(`#mentalCapacity-${applyForGrantOfProbateConfig.page7_mentalCapacityYes}`);
        I.click(`#courtOfProtection-${applyForGrantOfProbateConfig.page7_courtOfProtectionYes}`);
        I.click(`#epaOrLpa-${applyForGrantOfProbateConfig.page7_epaOrLpaYes}`);
        I.click(`#epaRegistered-${applyForGrantOfProbateConfig.page7_epaRegisteredYes}`);
    }

    if (crud === 'update') {
        I.selectOption('#selectionList', applyForGrantOfProbateConfig.page7_list1_update_option);
        I.click(commonConfig.continueButton);
        I.fillField('#attorneyOnBehalfOfNameAndAddress_0_name', applyForGrantOfProbateConfig.page7_representativeOfName_update);

    }

    I.click(commonConfig.continueButton);
};
