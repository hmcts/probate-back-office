'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateManualProbateManCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page1_waitForText, testConfig.WaitForTextTimeout);
        await I.waitForElement('#registryLocation');
        await I.selectOption('#registryLocation', createGrantOfProbateConfig.page1_list1_registry_location);
        await I.selectOption('#applicationType', createGrantOfProbateConfig.page1_list2_application_type);

        await I.fillField('#applicationSubmittedDate-day', createGrantOfProbateConfig.page1_applicationSubmittedDate_day);
        await I.fillField('#applicationSubmittedDate-month', createGrantOfProbateConfig.page1_applicationSubmittedDate_month);
        await I.fillField('#applicationSubmittedDate-year', createGrantOfProbateConfig.page1_applicationSubmittedDate_year);
        await I.click({css: `#paperForm_${createGrantOfProbateConfig.page1_optionNo}`});

        await I.selectOption('#caseType', createGrantOfProbateConfig.page1_list3_case_type);

        await I.fillField('#extraCopiesOfGrant', createGrantOfProbateConfig.page1_extraCopiesOfGrant);
        await I.fillField('#outsideUKGrantCopies', createGrantOfProbateConfig.page1_outsideUKGrantCopies);

        await I.waitForText(createGrantOfProbateConfig.page1_waitForText, testConfig.WaitForTextTimeout);
        await I.fillField('#primaryApplicantForenames', createGrantOfProbateConfig.page1_firstnames);
        await I.fillField('#primaryApplicantSurname', createGrantOfProbateConfig.page1_lastnames);
        await I.click(`#primaryApplicantIsApplying_${createGrantOfProbateConfig.page1_applyingYes}`);
        await I.fillField('#primaryApplicantEmailAddress', createGrantOfProbateConfig.page1_email);

        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayShort);
        }

        const pcLocator = {xpath: createGrantOfProbateConfig.UKpostcodeLink};
        await I.click(pcLocator);
        await I.waitForVisible({css: '#primaryApplicantAddress__detailAddressLine1'});
        await I.fillField('#primaryApplicantAddress__detailAddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#primaryApplicantAddress__detailAddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#primaryApplicantAddress__detailAddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#primaryApplicantAddress__detailPostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#primaryApplicantAddress__detailCounty', createGrantOfProbateConfig.address_county);
        await I.fillField('#primaryApplicantAddress__detailPostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#primaryApplicantAddress__detailCountry', createGrantOfProbateConfig.address_country);

        await I.click(`#otherExecutorExists_${createGrantOfProbateConfig.page1_otherExecutorExistsNo}`);
        await I.fillField({css: '#boDeceasedTitle'}, createGrantOfProbateConfig.page1_bo_deceasedTitle);

        await I.fillField({css: '#deceasedForenames'}, createGrantOfProbateConfig.page1_deceasedForenames);
        await I.fillField({css: '#deceasedSurname'}, createGrantOfProbateConfig.page1_deceasedSurname);
        await I.fillField('#boDeceasedHonours', createGrantOfProbateConfig.page1_bo_deceasedHonours);

        const pcLocator2 = {xpath: createGrantOfProbateConfig.UKpostcodeLink2};
        await I.waitForVisible(pcLocator2);
        await I.click(pcLocator2);

        await I.waitForVisible({css: '#deceasedAddress__detailAddressLine1'});
        await I.fillField('#deceasedAddress__detailAddressLine1', createGrantOfProbateConfig.address_line1);
        await I.fillField('#deceasedAddress__detailAddressLine2', createGrantOfProbateConfig.address_line2);
        await I.fillField('#deceasedAddress__detailAddressLine3', createGrantOfProbateConfig.address_line3);
        await I.fillField('#deceasedAddress__detailPostTown', createGrantOfProbateConfig.address_town);
        await I.fillField('#deceasedAddress__detailCounty', createGrantOfProbateConfig.address_county);
        await I.fillField('#deceasedAddress__detailPostCode', createGrantOfProbateConfig.address_postcode);
        await I.fillField('#deceasedAddress__detailCountry', createGrantOfProbateConfig.address_country);

        await I.selectOption({css: '#dateOfDeathType'}, createGrantOfProbateConfig.page1_dateOfDeathType);
        await I.fillField({css: '#deceasedDateOfBirth-day'}, createGrantOfProbateConfig.page1_deceasedDob_day);
        await I.fillField({css: '#deceasedDateOfBirth-month'}, createGrantOfProbateConfig.page1_deceasedDob_month);
        await I.fillField({css: '#deceasedDateOfBirth-year'}, createGrantOfProbateConfig.page1_deceasedDob_year);
        await I.fillField({css: '#deceasedDateOfDeath-day'}, createGrantOfProbateConfig.page1_deceasedDod_day);
        await I.fillField({css: '#deceasedDateOfDeath-month'}, createGrantOfProbateConfig.page1_deceasedDod_month);
        await I.fillField({css: '#deceasedDateOfDeath-year'}, createGrantOfProbateConfig.page1_deceasedDod_year);

        await I.click(`#deceasedAnyOtherNames_${createGrantOfProbateConfig.page1_deceasedAnyOtherNamesNo}`);

        await I.click('#deceasedDomicileInEngWales_Yes');

        await I.click({css: `#languagePreferenceWelsh_${createGrantOfProbateConfig.page1_optionNo}`});
    }
    await I.waitForNavigationToComplete(commonConfig.continueButton, 20);
};
