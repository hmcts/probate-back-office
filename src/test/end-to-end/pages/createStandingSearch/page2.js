'use strict';

const testConfig = require('src/test/config');
const createStandingSearchConfig = require('./createStandingSearchConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    if (crud === 'create') {
        I.waitForText(createStandingSearchConfig.page2_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#deceasedForenames', createStandingSearchConfig.page2_firstnames);
        I.fillField('#deceasedSurname', createStandingSearchConfig.page2_lastnames);

        I.fillField('#deceasedDateOfDeath-day', createStandingSearchConfig.page2_deceasedDod_day);
        I.fillField('#deceasedDateOfDeath-month', createStandingSearchConfig.page2_deceasedDod_month);
        I.fillField('#deceasedDateOfDeath-year', createStandingSearchConfig.page2_deceasedDod_year);

        I.fillField('#deceasedDateOfBirth-day', createStandingSearchConfig.page2_deceasedDob_day);
        I.fillField('#deceasedDateOfBirth-month', createStandingSearchConfig.page2_deceasedDob_month);
        I.fillField('#deceasedDateOfBirth-year', createStandingSearchConfig.page2_deceasedDob_year);

        I.click(createStandingSearchConfig.page2_deceasedAnyOtherNamesYes);
        I.waitForText(createStandingSearchConfig.page2_waitForText2, testConfig.TestTimeToWaitForText);
        I.click({type: 'button'}, '#deceasedFullAliasNameList>div');
        I.waitForText(createStandingSearchConfig.page2_waitForText3, testConfig.TestTimeToWaitForText);
        I.fillField('#deceasedFullAliasNameList_0_FullAliasName', createStandingSearchConfig.page2_deceasedAlias1);
        I.click({type: 'button'}, '#deceasedFullAliasNameList>div');
        I.seeElement('#deceasedFullAliasNameList_1_FullAliasName');
        I.fillField('#deceasedFullAliasNameList_1_FullAliasName', createStandingSearchConfig.page2_deceasedAlias2);

        I.click(createStandingSearchConfig.UKpostcodeLink);

        I.fillField('#deceasedAddress__AddressLine1', createStandingSearchConfig.address_line1);
        I.fillField('#deceasedAddress__AddressLine2', createStandingSearchConfig.address_line2);
        I.fillField('#deceasedAddress__AddressLine3', createStandingSearchConfig.address_line3);
        I.fillField('#deceasedAddress__PostTown', createStandingSearchConfig.address_town);
        I.fillField('#deceasedAddress__County', createStandingSearchConfig.address_county);
        I.fillField('#deceasedAddress__PostCode', createStandingSearchConfig.address_postcode);
        I.fillField('#deceasedAddress__Country', createStandingSearchConfig.address_country);

    }

    if (crud === 'update1') {
        createStandingSearchConfig.page2_firstnames_update = `${createStandingSearchConfig.page2_firstnames}_${crud}`;
        createStandingSearchConfig.page2_lastnames_update = `${createStandingSearchConfig.page2_lastnames}_${crud}`;

        I.waitForText(createStandingSearchConfig.page2_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#deceasedForenames', createStandingSearchConfig.page2_firstnames_update);
        I.fillField('#deceasedSurname', createStandingSearchConfig.page2_lastnames_update);

    }

    if (crud === 'update2') {
        createStandingSearchConfig.page2_firstnames_update = `${createStandingSearchConfig.page2_firstnames}_${crud}`;
        createStandingSearchConfig.page2_lastnames_update = `${createStandingSearchConfig.page2_lastnames}_${crud}`;

        I.waitForText(createStandingSearchConfig.page2_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#deceasedForenames', createStandingSearchConfig.page2_firstnames_update);
        I.fillField('#deceasedSurname', createStandingSearchConfig.page2_lastnames_update);

    }

    if (crud === 'update3') {
        createStandingSearchConfig.page2_firstnames_update = `${createStandingSearchConfig.page2_firstnames}_${crud}`;
        createStandingSearchConfig.page2_lastnames_update = `${createStandingSearchConfig.page2_lastnames}_${crud}`;

        I.waitForText(createStandingSearchConfig.page2_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#deceasedForenames', createStandingSearchConfig.page2_firstnames_update);
        I.fillField('#deceasedSurname', createStandingSearchConfig.page2_lastnames_update);

    }

    if (crud === 'update4') {
        createStandingSearchConfig.page2_firstnames_update = `${createStandingSearchConfig.page2_firstnames}_${crud}`;
        createStandingSearchConfig.page2_lastnames_update = `${createStandingSearchConfig.page2_lastnames}_${crud}`;

        I.waitForText(createStandingSearchConfig.page2_amend_waitForText, testConfig.TestTimeToWaitForText);
        I.fillField('#deceasedForenames', createStandingSearchConfig.page2_firstnames_update);
        I.fillField('#deceasedSurname', createStandingSearchConfig.page2_lastnames_update);

    }

    I.click(commonConfig.continueButton);
};
