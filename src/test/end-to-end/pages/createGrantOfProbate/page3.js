'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page3_waitForText, testConfig.TestTimeToWaitForText);
        await I.seeElement('#otherExecutorExists-Yes');
        await I.seeElement('#otherExecutorExists-No');
        await I.click(`#otherExecutorExists-${createGrantOfProbateConfig.page3_otherExecutorExistsYes}`);
        await I.waitForText(createGrantOfProbateConfig.page3_waitForText2, testConfig.TestTimeToWaitForText);
        await I.click({type: 'button'}, '#executorsApplying>div');
        await I.waitForText(createGrantOfProbateConfig.page3_waitForText3, testConfig.TestTimeToWaitForText);
        await I.fillField('#executorsApplying_0_applyingExecutorName', createGrantOfProbateConfig.page3_executor0_name);
        await I.fillField('#executorsApplying_0_applyingExecutorPhoneNumber', createGrantOfProbateConfig.page3_phone_number);
        await I.fillField('#executorsApplying_0_applyingExecutorEmail', createGrantOfProbateConfig.page3_applying_executor_email);
        await I.fillField('#executorsApplying_0_applyingExecutorOtherNames', createGrantOfProbateConfig.page3_executor0_alias);
        await I.selectOption('#executorsApplying_0_applyingExecutorOtherNamesReason', createGrantOfProbateConfig.page3_executor0_alias_reason);

        await I.click(createGrantOfProbateConfig.UKpostcodeLink);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress_AddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress_AddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress_AddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress_PostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress_County', createGrantOfProbateConfig.address_county);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress_PostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress_Country', createGrantOfProbateConfig.address_country);

        await I.click({type: 'button'}, '#executorsNotApplying>div');
        await I.wait(0.1); // needed in order to be able to switch off auto delay

        await I.waitForVisible('#executorsNotApplying_0_notApplyingExecutorNameOnWill');
        await I.fillField('#executorsNotApplying_0_notApplyingExecutorName', createGrantOfProbateConfig.page3_executor1_name);
        await I.fillField('#executorsNotApplying_0_notApplyingExecutorNameOnWill', createGrantOfProbateConfig.page3_executor1_alias);
        await I.fillField('#executorsNotApplying_0_notApplyingExecutorNameDifferenceComment', createGrantOfProbateConfig.page3_name_difference);
        await I.selectOption('#executorsNotApplying_0_notApplyingExecutorReason', createGrantOfProbateConfig.page3_not_applying_reason);
        await I.click(`#executorsNotApplying_0_notApplyingExecutorNotified-${createGrantOfProbateConfig.page3_notifiedYes}`);

        await I.click(`#notifiedApplicants-${createGrantOfProbateConfig.page3_notifiedApplicantsYes}`);
        await I.click(`#adopted-${createGrantOfProbateConfig.page3_adoptedYes}`);
        await I.click({type: 'button'}, '#adoptiveRelatives>div');

        await I.fillField('#adoptiveRelatives_0_name', createGrantOfProbateConfig.page3_adoptive_relative_name);
        await I.fillField('#adoptiveRelatives_0_relationship', createGrantOfProbateConfig.page3_adoptive_relative_relationship);
        await I.selectOption('#adoptiveRelatives_0_adoptedInOrOut', createGrantOfProbateConfig.page3_adoptive_adoptedInOrOut);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page3_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.waitForVisible('#executorsApplying_0_applyingExecutorOtherNames');
        await I.fillField('#executorsApplying_0_applyingExecutorOtherNames', createGrantOfProbateConfig.page3_executor0_alias_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
