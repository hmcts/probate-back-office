'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig = require('./createWillLodgementConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, unique_deceased_user) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createWillLodgementConfig.page2_waitForText, testConfig.WaitForTextTimeout);

        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayShort);
        }
        await I.fillField('#deceasedForenames', createWillLodgementConfig.page2_forenames + '_' + unique_deceased_user);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }

        await I.fillField('#deceasedSurname', createWillLodgementConfig.page2_surname + '_' + unique_deceased_user);

        await I.selectOption('#deceasedGender', createWillLodgementConfig.page2_gender);

        await I.fillField('#deceasedDateOfBirth-day', createWillLodgementConfig.page2_dateOfBirth_day);
        await I.fillField('#deceasedDateOfBirth-month', createWillLodgementConfig.page2_dateOfBirth_month);
        await I.fillField('#deceasedDateOfBirth-year', createWillLodgementConfig.page2_dateOfBirth_year);

        await I.fillField('#deceasedDateOfDeath-day', createWillLodgementConfig.page2_dateOfDeath_day);
        await I.fillField('#deceasedDateOfDeath-month', createWillLodgementConfig.page2_dateOfDeath_month);
        await I.fillField('#deceasedDateOfDeath-year', createWillLodgementConfig.page2_dateOfDeath_year);

        await I.fillField('#deceasedTypeOfDeath', createWillLodgementConfig.page2_typeOfDeath);

        await I.click(`#deceasedAnyOtherNames_${createWillLodgementConfig.page2_hasAliasYes}`);

        /* eslint-disable no-await-in-loop */
        let idx = 0;
        const keys = Object.keys(createWillLodgementConfig);
        for (let i=0; i < keys.length; i++) {
            const propName = keys[i];
            if (propName.includes('page2_alias_')) {
                await I.click(createWillLodgementConfig.page2_addAliasButton);
                await I.wait(testConfig.ManualDelayMedium); // implicit wait needed
                const locator = {css: `#deceasedFullAliasNameList_${idx}_FullAliasName`};
                if (!testConfig.TestAutoDelayEnabled) {
                    // only valid for local dev where we need it to run as fast as poss to minimise
                    // lost dev time
                    await I.wait(testConfig.ManualDelayShort);
                }
                await I.waitForVisible(locator);
                await I.fillField(locator, createWillLodgementConfig[propName]);
                idx += 1;
            }
        }
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayShort);
        }
        await I.fillField('#deceasedFullAliasNameList_0_FullAliasName', createWillLodgementConfig.page2_alias_1 + '_' + unique_deceased_user);

        await I.click(createWillLodgementConfig.UKpostcodeLink);
        await I.fillField('#deceasedAddress__detailAddressLine1', createWillLodgementConfig.address_line1);
        await I.fillField('#deceasedAddress__detailAddressLine2', createWillLodgementConfig.address_line2);
        await I.fillField('#deceasedAddress__detailAddressLine3', createWillLodgementConfig.address_line3);
        await I.fillField('#deceasedAddress__detailPostTown', createWillLodgementConfig.address_town);
        await I.fillField('#deceasedAddress__detailCounty', createWillLodgementConfig.address_county);
        await I.fillField('#deceasedAddress__detailPostCode', createWillLodgementConfig.address_postcode);
        await I.fillField('#deceasedAddress__detailCountry', createWillLodgementConfig.address_country);
        await I.fillField('#deceasedEmailAddress', createWillLodgementConfig.page2_email);
    }

    if (crud === 'update') {
        await I.waitForText(createWillLodgementConfig.page2_amend_waitForText, testConfig.WaitForTextTimeout);

        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayShort);
        }
        await I.fillField('#deceasedForenames', createWillLodgementConfig.page2_forenames + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }

        await I.fillField('#deceasedSurname', createWillLodgementConfig.page2_surname + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.fillField('#deceasedFullAliasNameList_0_FullAliasName', createWillLodgementConfig.page2_alias_1 + '_' + unique_deceased_user + ' UPDATED' + unique_deceased_user);

        await I.fillField('#deceasedDateOfDeath-day', createWillLodgementConfig.page2_dateOfDeath_day_update);
        await I.fillField('#deceasedDateOfDeath-month', createWillLodgementConfig.page2_dateOfDeath_month_update);
        await I.fillField('#deceasedDateOfDeath-year', createWillLodgementConfig.page2_dateOfDeath_year_update);
        await I.fillField('#deceasedDateOfBirth-day', createWillLodgementConfig.page2_dateOfBirth_day_update);
        await I.fillField('#deceasedDateOfBirth-month', createWillLodgementConfig.page2_dateOfBirth_month_update);
        await I.fillField('#deceasedDateOfBirth-year', createWillLodgementConfig.page2_dateOfBirth_year_update);
    }

    if (crud === 'update2orig') {

        // "reverting" update back to defaults - to enable case-match with matching case
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.waitForText(createWillLodgementConfig.page2_amend_waitForText, testConfig.WaitForTextTimeout);

        await I.fillField('#deceasedDateOfDeath-day', createWillLodgementConfig.page2_dateOfDeath_day);
        await I.fillField('#deceasedDateOfDeath-month', createWillLodgementConfig.page2_dateOfDeath_month);
        await I.fillField('#deceasedDateOfDeath-year', createWillLodgementConfig.page2_dateOfDeath_year);
        await I.fillField('#deceasedDateOfBirth-day', createWillLodgementConfig.page2_dateOfBirth_day);
        await I.fillField('#deceasedDateOfBirth-month', createWillLodgementConfig.page2_dateOfBirth_month);
        await I.fillField('#deceasedDateOfBirth-year', createWillLodgementConfig.page2_dateOfBirth_year);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
