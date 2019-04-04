'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createGrantOfProbateConfig.page3_waitForText, testConfig.TestTimeToWaitForText);
        I.click(`#otherExecutorExists-${createGrantOfProbateConfig.page3_otherExecutorExistsYes}`);
        I.waitForText(createGrantOfProbateConfig.page3_waitForText2, testConfig.TestTimeToWaitForText);
        I.click({type: 'button'}, '#executorsApplying>div');
        I.waitForText(createGrantOfProbateConfig.page3_waitForText3, testConfig.TestTimeToWaitForText);
        I.fillField('#executorsApplying_0_applyingExecutorName', createGrantOfProbateConfig.page3_executor0_name);
        I.fillField('#executorsApplying_0_applyingExecutorPhoneNumber', createGrantOfProbateConfig.page3_phone_number);
        I.fillField('#executorsApplying_0_applyingExecutorEmail', createGrantOfProbateConfig.page3_applying_executor_email);
        I.fillField('#executorsApplying_0_applyingExecutorOtherNames', createGrantOfProbateConfig.page3_executor0_alias);
        I.selectOption('#executorsApplying_0_applyingExecutorOtherNamesReason', createGrantOfProbateConfig.page3_executor0_alias_reason);

        I.click(createGrantOfProbateConfig.UKpostcodeLink);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_AddressLine1', createGrantOfProbateConfig.address_line1);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_AddressLine2', createGrantOfProbateConfig.address_line2);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_AddressLine3', createGrantOfProbateConfig.address_line3);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_PostTown', createGrantOfProbateConfig.address_town);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_County', createGrantOfProbateConfig.address_county);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_PostCode', createGrantOfProbateConfig.address_postcode);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_Country', createGrantOfProbateConfig.address_country);

        I.click({type: 'button'}, '#executorsNotApplying>div');
        I.fillField('#executorsNotApplying_0_notApplyingExecutorName', createGrantOfProbateConfig.page3_executor1_name);
        I.fillField('#executorsNotApplying_0_notApplyingExecutorNameOnWill', createGrantOfProbateConfig.page3_executor1_alias);
        I.fillField('#executorsNotApplying_0_notApplyingExecutorNameDifferenceComment', createGrantOfProbateConfig.page3_name_difference);
        I.selectOption('#executorsNotApplying_0_notApplyingExecutorReason', createGrantOfProbateConfig.page3_not_applying_reason);
        I.click(`#executorsNotApplying_0_notApplyingExecutorNotified-${createGrantOfProbateConfig.page3_notifiedYes}`);

        I.click(`#notifiedApplicants-${createGrantOfProbateConfig.page3_notifiedApplicantsYes}`);
        I.click(`#adopted-${createGrantOfProbateConfig.page3_adoptedYes}`);
        I.click({type: 'button'}, '#adoptiveRelatives>div');

        I.fillField('#adoptiveRelatives_0_name', createGrantOfProbateConfig.page3_adoptive_relative_name);
        I.fillField('#adoptiveRelatives_0_relationship', createGrantOfProbateConfig.page3_adoptive_relative_relationship);
        I.selectOption('#adoptiveRelatives_0_adoptedInOrOut', createGrantOfProbateConfig.page3_adoptive_adoptedInOrOut);

    }

    if (crud === 'update') {
        I.waitForText(createGrantOfProbateConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.selectOption('#selectionList', createGrantOfProbateConfig.page3_list1_update_option);
        I.click(commonConfig.continueButton);
        I.fillField('#executorsApplying_0_applyingExecutorOtherNames', createGrantOfProbateConfig.page3_executor0_alias_update);

    }

    I.click(commonConfig.continueButton);
};
