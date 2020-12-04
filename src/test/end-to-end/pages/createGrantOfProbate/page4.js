'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page4_waitForText, testConfig.TestTimeToWaitForText);
        await I.waitForElement({css: '#boDeceasedTitle'});
        await I.fillField({css: '#boDeceasedTitle'}, createGrantOfProbateConfig.page4_bo_deceasedTitle);

        await I.fillField({css: '#deceasedForenames'}, createGrantOfProbateConfig.page4_deceasedForenames);
        await I.fillField({css: '#deceasedSurname'}, createGrantOfProbateConfig.page4_deceasedSurname);
        await I.click({css: '#deceasedSurname'});

        const pcLocator = {css: createGrantOfProbateConfig.UKpostcodeLink};
        await I.waitForVisible(pcLocator);
        await I.click(pcLocator);

        await I.waitForVisible({css: '#deceasedAddress_AddressLine1'});
        await I.fillField('#deceasedAddress_AddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#deceasedAddress_AddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#deceasedAddress_AddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#deceasedAddress_PostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#deceasedAddress_County', createGrantOfProbateConfig.address_county);
        await I.fillField('#deceasedAddress_PostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#deceasedAddress_Country', createGrantOfProbateConfig.address_country);

        await I.fillField({css: '#deceasedDateOfBirth-day'}, createGrantOfProbateConfig.page4_deceasedDob_day);
        await I.fillField({css: '#deceasedDateOfBirth-month'}, createGrantOfProbateConfig.page4_deceasedDob_month);
        await I.fillField({css: '#deceasedDateOfBirth-year'}, createGrantOfProbateConfig.page4_deceasedDob_year);

        await I.selectOption({css: '#dateOfDeathType'}, createGrantOfProbateConfig.page4_dateOfDeathType);

        await I.fillField({css: '#deceasedDateOfDeath-day'}, createGrantOfProbateConfig.page4_deceasedDod_day);
        await I.fillField({css: '#deceasedDateOfDeath-month'}, createGrantOfProbateConfig.page4_deceasedDod_month);
        await I.fillField({css: '#deceasedDateOfDeath-year'}, createGrantOfProbateConfig.page4_deceasedDod_year);

        await I.click({css: '#deceasedAnyOtherNames-No'});
        await I.selectOption({css: '#deceasedMaritalStatus'}, createGrantOfProbateConfig.page4_deceasedMaritalStatus);

        await I.click({css: '#foreignAsset-No'});
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page3_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

        await I.fillField('#executorsApplying_0_applyingExecutorOtherNames', createGrantOfProbateConfig.page3_executor0_alias_update);
    }

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
