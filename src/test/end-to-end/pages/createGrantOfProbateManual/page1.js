'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateManualConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, unique_deceased_user, caseConfig = createGrantOfProbateConfig) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(caseConfig.page1_waitForText, testConfig.WaitForTextTimeout);
        await I.waitForElement('#registryLocation');
        await I.selectOption('#registryLocation', caseConfig.page1_list1_registry_location);
        await I.selectOption('#applicationType', caseConfig.page1_list2_application_type);

        await I.fillField('#applicationSubmittedDate-day', caseConfig.page1_applicationSubmittedDate_day);
        await I.fillField('#applicationSubmittedDate-month', caseConfig.page1_applicationSubmittedDate_month);
        await I.fillField('#applicationSubmittedDate-year', caseConfig.page1_applicationSubmittedDate_year);
        await I.click({css: `#paperForm_${caseConfig.page1_optionNo}`});

        await I.selectOption('#caseType', caseConfig.page1_list3_case_type);

        await I.fillField('#extraCopiesOfGrant', caseConfig.page1_extraCopiesOfGrant);
        await I.fillField('#outsideUKGrantCopies', caseConfig.page1_outsideUKGrantCopies);

        await I.waitForText(caseConfig.page1_waitForText, testConfig.WaitForTextTimeout);
        await I.fillField('#primaryApplicantForenames', caseConfig.page1_firstnames);
        await I.fillField('#primaryApplicantSurname', caseConfig.page1_lastnames);
        await I.click(`#primaryApplicantIsApplying_${caseConfig.page1_applyingYes}`);
        await I.fillField('#primaryApplicantEmailAddress', caseConfig.page1_email);

        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayShort);
        }

        const pcLocator = {xpath: caseConfig.UKpostcodeLink};
        await I.click(pcLocator);
        await I.waitForVisible({css: '#primaryApplicantAddress__detailAddressLine1'});
        await I.fillField('#primaryApplicantAddress__detailAddressLine1', caseConfig.address_line1);
        await I.fillField('#primaryApplicantAddress__detailAddressLine2', caseConfig.address_line2);
        await I.fillField('#primaryApplicantAddress__detailAddressLine3', caseConfig.address_line3);
        await I.fillField('#primaryApplicantAddress__detailPostTown', caseConfig.address_town);
        await I.fillField('#primaryApplicantAddress__detailCounty', caseConfig.address_county);
        await I.fillField('#primaryApplicantAddress__detailPostCode', caseConfig.address_postcode);
        await I.fillField('#primaryApplicantAddress__detailCountry', caseConfig.address_country);

        await I.click(`#otherExecutorExists_${caseConfig.page1_otherExecutorExistsNo}`);
        await I.fillField({css: '#boDeceasedTitle'}, caseConfig.page1_bo_deceasedTitle);

        if (unique_deceased_user === 'No') {
            await I.fillField({css: '#deceasedForenames'}, caseConfig.page1_deceasedForenames);
            await I.fillField({css: '#deceasedSurname'}, caseConfig.page1_deceasedSurname);
        } else {
            await I.fillField({css: '#deceasedForenames'}, caseConfig.page1_deceasedForenames + '_' + unique_deceased_user);
            await I.fillField({css: '#deceasedSurname'}, caseConfig.page1_deceasedSurname + '_' + unique_deceased_user);
        }
        await I.fillField('#boDeceasedHonours', caseConfig.page1_bo_deceasedHonours);

        const pcLocator2 = {xpath: caseConfig.UKpostcodeLink2};
        await I.waitForVisible(pcLocator2);
        await I.click(pcLocator2);

        await I.waitForVisible({css: '#deceasedAddress__detailAddressLine1'});
        await I.fillField('#deceasedAddress__detailAddressLine1', caseConfig.address_line1);
        await I.fillField('#deceasedAddress__detailAddressLine2', caseConfig.address_line2);
        await I.fillField('#deceasedAddress__detailAddressLine3', caseConfig.address_line3);
        await I.fillField('#deceasedAddress__detailPostTown', caseConfig.address_town);
        await I.fillField('#deceasedAddress__detailCounty', caseConfig.address_county);
        await I.fillField('#deceasedAddress__detailPostCode', caseConfig.address_postcode);
        await I.fillField('#deceasedAddress__detailCountry', caseConfig.address_country);

        await I.selectOption({css: '#dateOfDeathType'}, caseConfig.page1_dateOfDeathType);
        await I.fillField({css: '#deceasedDateOfBirth-day'}, caseConfig.page1_deceasedDob_day);
        await I.fillField({css: '#deceasedDateOfBirth-month'}, caseConfig.page1_deceasedDob_month);
        await I.fillField({css: '#deceasedDateOfBirth-year'}, caseConfig.page1_deceasedDob_year);
        await I.fillField({css: '#deceasedDateOfDeath-day'}, caseConfig.page1_deceasedDod_day);
        await I.fillField({css: '#deceasedDateOfDeath-month'}, caseConfig.page1_deceasedDod_month);
        await I.fillField({css: '#deceasedDateOfDeath-year'}, caseConfig.page1_deceasedDod_year);

        await I.click(`#deceasedAnyOtherNames_${caseConfig.page1_deceasedAnyOtherNamesNo}`);

        await I.click('#deceasedDomicileInEngWales_Yes');

        await I.click({css: `#languagePreferenceWelsh_${caseConfig.page1_optionNo}`});
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
