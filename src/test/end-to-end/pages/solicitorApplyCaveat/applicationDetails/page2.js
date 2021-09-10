'use strict';

const applicationDetailsConfig = require('./applicationDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#deceasedForenames');
    await I.runAccessibilityTest();
    await I.fillField('#deceasedForenames', applicationDetailsConfig.page2_deceased_forename);
    await I.fillField('#deceasedSurname', applicationDetailsConfig.page2_deceased_surname);

    await I.fillField('#deceasedDateOfDeath-day', applicationDetailsConfig.page2_dateOfDeath_day);
    await I.fillField('#deceasedDateOfDeath-month', applicationDetailsConfig.page2_dateOfDeath_month);
    await I.fillField('#deceasedDateOfDeath-year', applicationDetailsConfig.page2_dateOfDeath_year);

    await I.fillField('#deceasedDateOfBirth-day', applicationDetailsConfig.page2_dateOfBirth_day);
    await I.fillField('#deceasedDateOfBirth-month', applicationDetailsConfig.page2_dateOfBirth_month);
    await I.fillField('#deceasedDateOfBirth-year', applicationDetailsConfig.page2_dateOfBirth_year);

    await I.click(`#deceasedAnyOtherNames_${applicationDetailsConfig.page2_hasAliasYes}`);
    if (!testConfig.TestAutoDelayEnabled) {
        // only valid for local dev where we need it to run as fast as poss to minimise
        // lost dev time
        await I.wait(testConfig.ManualDelayShort);
    }

    let idx = 0;
    /* eslint-disable no-await-in-loop */
    const keys = Object.keys(applicationDetailsConfig);
    for (let i=0; i < keys.length; i++) {
        const propName = keys[i];
        if (propName.includes('page2_alias_')) {
            await I.click(applicationDetailsConfig.page2_addAliasButton);
            if (!testConfig.TestAutoDelayEnabled) {
                // only valid for local dev where we need it to run as fast as poss to minimise
                // lost dev time
                await I.wait(testConfig.ManualDelayShort);
            }
            const locator = {css: `#deceasedFullAliasNameList_${idx}_FullAliasName`};
            await I.waitForVisible(locator);
            await I.fillField(locator, applicationDetailsConfig[propName]);
            idx += 1;
        }
    }

    await I.click(applicationDetailsConfig.UKpostcodeLink);
    await I.fillField('#deceasedAddress__detailAddressLine1', applicationDetailsConfig.address_line1);
    await I.fillField('#deceasedAddress__detailAddressLine2', applicationDetailsConfig.address_line2);
    await I.fillField('#deceasedAddress__detailAddressLine3', applicationDetailsConfig.address_line3);
    await I.fillField('#deceasedAddress__detailPostTown', applicationDetailsConfig.address_town);
    await I.fillField('#deceasedAddress__detailCounty', applicationDetailsConfig.address_county);
    await I.fillField('#deceasedAddress__detailPostCode', applicationDetailsConfig.address_postcode);
    await I.fillField('#deceasedAddress__detailCountry', applicationDetailsConfig.address_country);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
