'use strict';

const testConfig = require('src/test/config');
const applyForGrantOfProbateConfig = require('./applyForGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    I.waitForText(applyForGrantOfProbateConfig.page3_waitForText, testConfig.TestTimeToWaitForText);

    if (crud === 'create') {
        I.click(`#otherExecutorExists-${applyForGrantOfProbateConfig.page3_otherExecutorExistsYes}`);
        I.click('#executorsApplying > div > button');
        I.fillField('#executorsApplying_0_applyingExecutorName', applyForGrantOfProbateConfig.page3_executor0_name);
        I.fillField('#executorsApplying_0_applyingExecutorPhoneNumber', applyForGrantOfProbateConfig.page3_phone_number);
        I.fillField('#executorsApplying_0_applyingExecutorEmail', applyForGrantOfProbateConfig.page3_applying_executor_email);
        I.fillField('#executorsApplying_0_applyingExecutorOtherNames', applyForGrantOfProbateConfig.page3_executor0_alias);
        I.selectOption('#executorsApplying_0_applyingExecutorOtherNamesReason', applyForGrantOfProbateConfig.page3_executor0_alias_reason);

        I.click(applyForGrantOfProbateConfig.UKpostcodeLink);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_AddressLine1', applyForGrantOfProbateConfig.address_line1);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_AddressLine2', applyForGrantOfProbateConfig.address_line2);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_AddressLine3', applyForGrantOfProbateConfig.address_line3);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_PostTown', applyForGrantOfProbateConfig.address_town);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_County', applyForGrantOfProbateConfig.address_county);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_PostCode', applyForGrantOfProbateConfig.address_postcode);
        I.fillField('#executorsApplying_0_applyingExecutorAddress_Country', applyForGrantOfProbateConfig.address_country);

        I.click('#executorsNotApplying > div > button');
        I.fillField('#executorsNotApplying_0_notApplyingExecutorName', applyForGrantOfProbateConfig.page3_executor1_name);
        I.fillField('#executorsNotApplying_0_notApplyingExecutorNameOnWill', applyForGrantOfProbateConfig.page3_executor1_alias);
        I.fillField('#executorsNotApplying_0_notApplyingExecutorNameDifferenceComment', applyForGrantOfProbateConfig.page3_name_difference);
        I.selectOption('#executorsNotApplying_0_notApplyingExecutorReason', applyForGrantOfProbateConfig.page3_not_applying_reason);
        I.click(`#executorsNotApplying_0_notApplyingExecutorNotified-${applyForGrantOfProbateConfig.page3_notifiedYes}`);

        I.click(`#notifiedApplicants-${applyForGrantOfProbateConfig.page3_notifiedApplicantsYes}`);
        I.click(`#adopted-${applyForGrantOfProbateConfig.page3_adoptedYes}`);
        I.click('#adoptiveRelatives > div > button');

        I.fillField('#adoptiveRelatives_0_name', applyForGrantOfProbateConfig.page3_adoptive_relative_name);
        I.fillField('#adoptiveRelatives_0_relationship', applyForGrantOfProbateConfig.page3_adoptive_relative_relationship);
        I.selectOption('#adoptiveRelatives_0_adoptedInOrOut', applyForGrantOfProbateConfig.page3_adoptive_adoptedInOrOut);

    }

    if (crud === 'update') {
        I.fillField('#lodgedDate-day', applyForGrantOfProbateConfig.page1_lodgedDate_day_update);
        I.fillField('#lodgedDate-month', applyForGrantOfProbateConfig.page1_lodgedDate_month_update);
        I.fillField('#lodgedDate-year', applyForGrantOfProbateConfig.page1_lodgedDate_year_update);

        I.fillField('#numberOfCodicils', applyForGrantOfProbateConfig.page1_numberOfCodicils_update);

    }

    I.click(commonConfig.continueButton);
};
