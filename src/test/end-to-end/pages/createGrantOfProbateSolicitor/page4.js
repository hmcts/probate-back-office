'use strict';

const assert = require('assert');
const testConfig = require('src/test/config');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, createGrantOfProbateConfig) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page4_waitForText, testConfig.WaitForTextTimeout);
        await I.seeElement('#otherExecutorExists_Yes');
        await I.seeElement('#otherExecutorExists_No');
        await I.click(`#otherExecutorExists_${createGrantOfProbateConfig.page4_otherExecutorExistsYes}`);
        await I.waitForText(createGrantOfProbateConfig.page4_waitForText2, testConfig.WaitForTextTimeout);
        await I.click({type: 'button'}, '#executorsApplying>div');
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.waitForText(createGrantOfProbateConfig.page4_waitForText3, testConfig.WaitForTextTimeout);

        let numEls = await I.grabNumberOfVisibleElements({css: '#executorsApplying_0_applyingExecutorTrustCorpPosition'});
        assert (numEls === 0);
        await I.selectOption({css: '#executorsApplying_0_applyingExecutorType'}, createGrantOfProbateConfig.page4_executor0_executorType);
        if (createGrantOfProbateConfig.page4_executor0_executorType === 'Trust corporation') {
            await I.waitForVisible({css: '#executorsApplying_0_applyingExecutorTrustCorpPosition'});
            await I.fillField({css: '#executorsApplying_0_applyingExecutorTrustCorpPosition'}, createGrantOfProbateConfig.page4_executor0_trustCorpPos);
        }

        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }

        await I.fillField({css: '#executorsApplying_0_applyingExecutorName'}, createGrantOfProbateConfig.page4_executor0_name);
        await I.fillField({css: '#executorsApplying_0_applyingExecutorFirstName'}, createGrantOfProbateConfig.page4_executor0_firstName);
        await I.fillField({css: '#executorsApplying_0_applyingExecutorLastName'}, createGrantOfProbateConfig.page4_executor0_lastName);

        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.fillField({css: '#executorsApplying_0_applyingExecutorPhoneNumber'}, createGrantOfProbateConfig.page4_phone_number);
        await I.fillField('#executorsApplying_0_applyingExecutorEmail', createGrantOfProbateConfig.page4_applying_executor_email);
        await I.fillField('#executorsApplying_0_applyingExecutorOtherNames', createGrantOfProbateConfig.page4_executor0_alias);
        await I.selectOption('#executorsApplying_0_applyingExecutorOtherNamesReason', createGrantOfProbateConfig.page4_executor0_alias_reason);

        await I.click(createGrantOfProbateConfig.UKpostcodeLink);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress__detailAddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress__detailAddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress__detailAddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress__detailPostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress__detailCounty', createGrantOfProbateConfig.address_county);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress__detailPostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#executorsApplying_0_applyingExecutorAddress__detailCountry', createGrantOfProbateConfig.address_country);

        await I.click({type: 'button'}, '#executorsNotApplying>div');
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }

        await I.waitForVisible('#executorsNotApplying_0_notApplyingExecutorName');
        await I.fillField('#executorsNotApplying_0_notApplyingExecutorName', createGrantOfProbateConfig.page4_executor1_name);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.fillField('#executorsNotApplying_0_notApplyingExecutorNameOnWill', createGrantOfProbateConfig.page4_executor1_alias);
        await I.fillField('#executorsNotApplying_0_notApplyingExecutorNameDifferenceComment', createGrantOfProbateConfig.page4_name_difference);

        numEls = await I.grabNumberOfVisibleElements({css: '#executorsNotApplying_0_notApplyingExecutorDispenseWithNotice_Yes'});
        assert (numEls === 0);

        numEls = await I.grabNumberOfVisibleElements({css: '#executorsNotApplying_0_notApplyingExecutorDispenseWithNoticeLeaveGiven_Yes'});
        assert (numEls === 0);

        numEls = await I.grabNumberOfVisibleElements({css: '#notApplyingExecutorDispenseWithNoticeLeaveGivenDate-day'});
        assert (numEls === 0);

        await I.waitForEnabled('#executorsNotApplying_0_notApplyingExecutorReason');
        await I.selectOption('#executorsNotApplying_0_notApplyingExecutorReason', createGrantOfProbateConfig.page4_not_applying_reason);

        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(0.1);
        }

        numEls = await I.grabNumberOfVisibleElements({css: '#executorsNotApplying_0_notApplyingExecutorDispenseWithNoticeLeaveGiven_Yes'});
        assert (numEls === 0);

        numEls = await I.grabNumberOfVisibleElements({css: '#notApplyingExecutorDispenseWithNoticeLeaveGivenDate-day'});
        assert (numEls === 0);

        await I.waitForVisible('#executorsNotApplying_0_notApplyingExecutorDispenseWithNotice_Yes');
        await I.waitForEnabled('#executorsNotApplying_0_notApplyingExecutorDispenseWithNotice_Yes');
        await I.click('#executorsNotApplying_0_notApplyingExecutorDispenseWithNotice_Yes');
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(0.1);
        }

        numEls = await I.grabNumberOfVisibleElements({css: '#notApplyingExecutorDispenseWithNoticeLeaveGivenDate-day'});
        assert (numEls === 0);

        // await I.click('#executorsNotApplying_0_notApplyingExecutorDispenseWithNoticeLeaveGiven_No');
        await I.click('#executorsNotApplying_0_notApplyingExecutorDispenseWithNoticeLeaveGiven_Yes');

        await I.waitForVisible({css: '#notApplyingExecutorDispenseWithNoticeLeaveGivenDate-day'});

        await I.fillField('#notApplyingExecutorDispenseWithNoticeLeaveGivenDate-day', createGrantOfProbateConfig.page4_dispense_notice_leave_day);
        await I.fillField('#notApplyingExecutorDispenseWithNoticeLeaveGivenDate-month', createGrantOfProbateConfig.page4_dispense_notice_leave_month);
        await I.fillField('#notApplyingExecutorDispenseWithNoticeLeaveGivenDate-year', createGrantOfProbateConfig.page4_dispense_notice_leave_year);

        await I.click(`#executorsNotApplying_0_notApplyingExecutorNotified_${createGrantOfProbateConfig.page4_notifiedYes}`);

        await I.click(`#notifiedApplicants_${createGrantOfProbateConfig.page4_notifiedApplicantsYes}`);
        await I.click(`#adopted_${createGrantOfProbateConfig.page4_adoptedYes}`);
        await I.click({type: 'button'}, '#adoptiveRelatives>div');
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.waitForVisible('#adoptiveRelatives_0_name');
        await I.fillField('#adoptiveRelatives_0_name', createGrantOfProbateConfig.page4_adoptive_relative_name);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.fillField('#adoptiveRelatives_0_relationship', createGrantOfProbateConfig.page4_adoptive_relative_relationship);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.selectOption('#adoptiveRelatives_0_adoptedInOrOut', createGrantOfProbateConfig.page4_adoptive_adoptedInOrOut);
    }

    if (crud === 'update') {
        await I.waitForText(createGrantOfProbateConfig.page4_amend_waitForText, testConfig.WaitForTextTimeout);
        await I.waitForEnabled('#selectionList');
        await I.selectOption('#selectionList', createGrantOfProbateConfig.page4_list1_update_option);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.waitForVisible('#executorsApplying_0_applyingExecutorOtherNames');
        await I.fillField('#executorsApplying_0_applyingExecutorOtherNames', createGrantOfProbateConfig.page4_executor0_alias_update);

        const numEls = await I.grabNumberOfVisibleElements({css: '#executorsApplying_0_applyingExecutorTrustCorpPosition'});
        if (createGrantOfProbateConfig.page4_executor0_executorType === 'Trust corporation') {
            assert (numEls === 1);
        } else {
            assert (numEls === 0);
        }
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
