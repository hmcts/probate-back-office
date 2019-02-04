'use strict';

const testConfig = require('src/test/config.js');
const createWillLodgementConfig = require('./createWillLodgementConfig.json');
const {forEach} = require('lodash');

module.exports = function () {

    const I = this;

    I.waitForText(createWillLodgementConfig.page3.waitForText, testConfig.TestTimeToWaitForText);
  //  I.amOnPage(createWillLodgementConfig.pageUrl);

    createWillLodgementConfig.page3.executorList.forEach(function(value,key) {
        if (key === 0) {
            I.fillField('#executorTitle', value.title);
            I.fillField('#executorForenames', value.firstnames);
            I.fillField('#executorSurname', value.lastnames);

            I.click(createWillLodgementConfig.common.enterUKpostcodeLink);
            I.fillField('#AddressLine1', createWillLodgementConfig.page3.common.address.line1);
            I.fillField('#AddressLine2', createWillLodgementConfig.page3.common.address.line2);
            I.fillField('#AddressLine3', createWillLodgementConfig.page3.common.address.line3);
            I.fillField('#PostTown', createWillLodgementConfig.page3.common.address.town);
            I.fillField('#County', createWillLodgementConfig.page3.common.address.county);
            I.fillField('#PostCode', createWillLodgementConfig.page3.common.address.postcode);
            I.fillField('#Country', createWillLodgementConfig.page3.common.address.country);
            I.fillField('#executorEmailAddress', value.email);
        } else {
            let index = parseInt(key) - 1;
            I.click(createWillLodgementConfig.page3.common.addAnotherExecutor);

            I.fillField(`#additionalExecutorList_${index}_executorTitle`, value.title);
            I.fillField(`#additionalExecutorList_${index}_executorForenames`, value.firstnames);
            I.fillField(`#additionalExecutorList_${index}_executorSurname`, value.lastnames);

            I.click(createWillLodgementConfig.common.enterUKpostcodeLink);

            I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine1`, createWillLodgementConfig.page3.common.address.line1);
            I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine2`, createWillLodgementConfig.page3.common.address.line2);
            I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine3`, createWillLodgementConfig.page3.common.address.line3);
            I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #PostTown`, createWillLodgementConfig.page3.common.address.town);
            I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #County`, createWillLodgementConfig.page3.common.address.county);
            I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #PostCode`, createWillLodgementConfig.page3.common.address.postcode);
            I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #Country`, createWillLodgementConfig.page3.common.address.country);
            I.fillField(`#additionalExecutorList_${index}_executorEmailAddress`, value.email);
        }
        //            additionalExecutorList_0_executorTitle
    })
        I.click(createWillLodgementConfig.common.locator);
};
