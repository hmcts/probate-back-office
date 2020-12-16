'use strict';

const applicationDetailsConfig = require('./applicationDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#deceasedForenames');

    await I.fillField('#deceasedForenames', applicationDetailsConfig.page2_deceased_forename);
    await I.fillField('#deceasedSurname', applicationDetailsConfig.page2_deceased_surname);

    await I.fillField('#deceasedDateOfDeath-day', applicationDetailsConfig.page2_dateOfDeath_day);
    await I.fillField('#deceasedDateOfDeath-month', applicationDetailsConfig.page2_dateOfDeath_month);
    await I.fillField('#deceasedDateOfDeath-year', applicationDetailsConfig.page2_dateOfDeath_year);

    await I.fillField('#deceasedDateOfBirth-day', applicationDetailsConfig.page2_dateOfBirth_day);
    await I.fillField('#deceasedDateOfBirth-month', applicationDetailsConfig.page2_dateOfBirth_month);
    await I.fillField('#deceasedDateOfBirth-year', applicationDetailsConfig.page2_dateOfBirth_year);

    await I.click(`#deceasedAnyOtherNames-${applicationDetailsConfig.page2_hasAliasYes}`);

    let idx = 0;
    /* eslint-disable no-await-in-loop */
    const keys = Object.keys(applicationDetailsConfig);
    for (let i=0; i < keys.length; i++) {
        const propName = keys[i];
        if (propName.includes('page2_alias_')) {
            await I.click(applicationDetailsConfig.page2_addAliasButton);
            await I.wait(0.1); // implicit wait needed here
            const locator = {css: `#deceasedFullAliasNameList_${idx}_FullAliasName`};
            await I.waitForVisible(locator);
            await I.fillField(locator, applicationDetailsConfig[propName]);
            idx += 1;
        }
    }

    await I.click(applicationDetailsConfig.UKpostcodeLink);
    await I.fillField('#deceasedAddress_AddressLine1', applicationDetailsConfig.address_line1);
    await I.fillField('#deceasedAddress_AddressLine2', applicationDetailsConfig.address_line2);
    await I.fillField('#deceasedAddress_AddressLine3', applicationDetailsConfig.address_line3);
    await I.fillField('#deceasedAddress_PostTown', applicationDetailsConfig.address_town);
    await I.fillField('#deceasedAddress_County', applicationDetailsConfig.address_county);
    await I.fillField('#deceasedAddress_PostCode', applicationDetailsConfig.address_postcode);
    await I.fillField('#deceasedAddress_Country', applicationDetailsConfig.address_country);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
