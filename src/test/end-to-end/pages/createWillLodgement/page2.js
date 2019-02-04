'use strict';

const testConfig = require('src/test/config.js');
const createWillLodgementConfig = require('./createWillLodgementConfig.json');
const {forEach} = require('lodash');

module.exports = function () {

    const I = this;

    I.waitForText(createWillLodgementConfig.page2.waitForText, testConfig.TestTimeToWaitForText);
  //  I.amOnPage(createWillLodgementConfig.pageUrl);

    I.fillField('#deceasedForenames',createWillLodgementConfig.page2.testator.firstnames);
    I.fillField('#deceasedSurname',createWillLodgementConfig.page2.testator.lastnames);

    I.selectOption(createWillLodgementConfig.page2.testator.genderList.id, createWillLodgementConfig.page2.testator.genderList.text);

    I.fillField('#deceasedDateOfBirth-day', createWillLodgementConfig.page2.testator.dateOfBirth.day);
    I.fillField('#deceasedDateOfBirth-month', createWillLodgementConfig.page2.testator.dateOfBirth.month);
    I.fillField('#deceasedDateOfBirth-year', createWillLodgementConfig.page2.testator.dateOfBirth.year);

    I.fillField('#deceasedDateOfDeath-day', createWillLodgementConfig.page2.testator.dateOfDeath.day);
    I.fillField('#deceasedDateOfDeath-month', createWillLodgementConfig.page2.testator.dateOfDeath.month);
    I.fillField('#deceasedDateOfDeath-year', createWillLodgementConfig.page2.testator.dateOfDeath.year);

    I.fillField('#deceasedTypeOfDeath', createWillLodgementConfig.page2.testator.typeOfDeath);

    I.click(`#deceasedAnyOtherNames-${createWillLodgementConfig.page2.testator.alias.hasAlias}`);

    forEach(createWillLodgementConfig.page2.testator.alias.names,function(value,key) {
        let index = parseInt(key) - 1;
        I.click(createWillLodgementConfig.page2.testator.alias.addAliasButton);
        I.fillField(`#deceasedFullAliasNameList_${index}_FullAliasName`, value);
    });


    I.click(createWillLodgementConfig.common.enterUKpostcodeLink);
    I.fillField('#AddressLine1', createWillLodgementConfig.page2.testator.address.line1);
    I.fillField('#AddressLine2', createWillLodgementConfig.page2.testator.address.line2);
    I.fillField('#AddressLine3', createWillLodgementConfig.page2.testator.address.line3);
    I.fillField('#PostTown', createWillLodgementConfig.page2.testator.address.town);
    I.fillField('#County', createWillLodgementConfig.page2.testator.address.county);
    I.fillField('#PostCode', createWillLodgementConfig.page2.testator.address.postcode);
    I.fillField('#Country', createWillLodgementConfig.page2.testator.address.country);
    I.fillField('#deceasedEmailAddress', createWillLodgementConfig.page2.testator.email);

    I.click(createWillLodgementConfig.common.locator);
};
