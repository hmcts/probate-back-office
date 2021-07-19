'use strict';

const testConfig = require('src/test/config');
const createCaveatConfig = require('./createCaveatConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, unique_deceased_user) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createCaveatConfig.page2_waitForText, testConfig.WaitForTextTimeout);

        await I.fillField('#deceasedForenames', createCaveatConfig.page2_forenames+unique_deceased_user);
        await I.fillField('#deceasedSurname', createCaveatConfig.page2_surname+unique_deceased_user);

        await I.fillField('#deceasedDateOfDeath-day', createCaveatConfig.page2_dateOfDeath_day);
        await I.fillField('#deceasedDateOfDeath-month', createCaveatConfig.page2_dateOfDeath_month);
        await I.fillField('#deceasedDateOfDeath-year', createCaveatConfig.page2_dateOfDeath_year);

        await I.fillField('#deceasedDateOfBirth-day', createCaveatConfig.page2_dateOfBirth_day);
        await I.fillField('#deceasedDateOfBirth-month', createCaveatConfig.page2_dateOfBirth_month);
        await I.fillField('#deceasedDateOfBirth-year', createCaveatConfig.page2_dateOfBirth_year);

        await I.click(`#deceasedAnyOtherNames_${createCaveatConfig.page2_hasAliasYes}`);

        let idx = 0;
        /* eslint-disable no-await-in-loop */
        const keys = Object.keys(createCaveatConfig);
        for (let i=0; i < keys.length; i++) {
            const propName = keys[i];
            if (propName.includes('page2_alias_')) {
                await I.click(createCaveatConfig.page2_addAliasButton);
                if (!testConfig.TestAutoDelayEnabled) {
                    await I.wait(testConfig.ManualDelayShort); // implicit wait needed here
                }
                const locator = {css: `#deceasedFullAliasNameList_${idx}_FullAliasName`};
                await I.waitForVisible(locator);
                await I.fillField(locator, createCaveatConfig[propName]);
                idx += 1;
            }
        }

        await I.click(createCaveatConfig.UKpostcodeLink);
        if (!testConfig.TestAutoDelayEnabled) {
            await I.wait(testConfig.ManualDelayShort); // implicit wait needed here
        }
        await I.fillField('#deceasedAddress__detailAddressAddressLine1', createCaveatConfig.address_line1);
        await I.fillField('#deceasedAddress__detailAddressAddressLine2', createCaveatConfig.address_line2);
        await I.fillField('#deceasedAddress__detailAddressAddressLine3', createCaveatConfig.address_line3);
        await I.fillField('#deceasedAddress__detailAddressPostTown', createCaveatConfig.address_town);
        await I.fillField('#deceasedAddress__detailAddressCounty', createCaveatConfig.address_county);
        await I.fillField('#deceasedAddress__detailAddressPostCode', createCaveatConfig.address_postcode);
        await I.fillField('#deceasedAddress__detailAddressCountry', createCaveatConfig.address_country);
    }

    if (crud === 'update') {
        await I.waitForText(createCaveatConfig.page2_amend_waitForText, testConfig.WaitForTextTimeout);
        await I.fillField('#deceasedForenames', createCaveatConfig.page2_forenames_update+unique_deceased_user);
        await I.fillField('#deceasedSurname', createCaveatConfig.page2_surname_update+unique_deceased_user);
        await I.fillField('#deceasedDateOfBirth-day', createCaveatConfig.page2_dateOfBirth_day_update);
        await I.fillField('#deceasedDateOfBirth-month', createCaveatConfig.page2_dateOfBirth_month_update);
        await I.fillField('#deceasedDateOfBirth-year', createCaveatConfig.page2_dateOfBirth_year_update);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
