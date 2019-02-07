'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig2 = require('./createWillLodgementFastConfig2');

module.exports = function () {

    const I = this;

    I.waitForText(createWillLodgementConfig2.page3_waitForText, testConfig.TestTimeToWaitForText);

    let index = 0;
    let executorFieldList = [];
    let additionalExecutorFieldList = [];

    Object.keys(createWillLodgementConfig2).forEach( function (value) {
        //const result = value.filter(word => word.toLowerCase().indexOf(`page3_executor${counter}`.toLowerCase()) > -1);
        if (value.includes(`page3_executor${index}`)) {
            executorFieldList.push(value);
        }
    });

    I.fillField('#executorTitle', createWillLodgementConfig2[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title`)]]);
    I.fillField('#executorForenames', createWillLodgementConfig2[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_firstnames`)]]);
    I.fillField('#executorSurname', createWillLodgementConfig2[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_lastnames`)]]);
    I.fillField('#executorEmailAddress', createWillLodgementConfig2[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email`)]]);

    I.click(createWillLodgementConfig2.UKpostcodeLink);
    I.fillField('#AddressLine1', createWillLodgementConfig2.address_line1);
    I.fillField('#AddressLine2', createWillLodgementConfig2.address_line2);
    I.fillField('#AddressLine3', createWillLodgementConfig2.address_line3);
    I.fillField('#PostTown', createWillLodgementConfig2.address_town);
    I.fillField('#County', createWillLodgementConfig2.address_county);
    I.fillField('#PostCode', createWillLodgementConfig2.address_postcode);
    I.fillField('#Country', createWillLodgementConfig2.address_country);

    Object.keys(createWillLodgementConfig2).forEach(function (value) {
        //const result = value.filter(word => word.toLowerCase().indexOf(`page3_executor${counter}`.toLowerCase()) > -1);
        if (value.includes('page3_additional_executor')) {
            additionalExecutorFieldList.push(value);
        }
    });

    I.fillField(`#additionalExecutorList_${index}_executorTitle`, createWillLodgementConfig2[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_title`)]]);
    I.fillField(`#additionalExecutorList_${index}_executorForenames`, createWillLodgementConfig2[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_firstnames`)]]);
    I.fillField(`#additionalExecutorList_${index}_executorSurname`, createWillLodgementConfig2[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_lastnames`)]]);

    I.click(createWillLodgementConfig2.UKpostcodeLink);

    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine1`, createWillLodgementConfig2.address_line1);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine2`, createWillLodgementConfig2.address_line2);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine3`, createWillLodgementConfig2.address_line3);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #PostTown`, createWillLodgementConfig2.address_town);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #County`, createWillLodgementConfig2.address_county);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #PostCode`, createWillLodgementConfig2.address_postcode);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #Country`, createWillLodgementConfig2.address_country);

    I.fillField(`#additionalExecutorList_${index}_executorEmailAddress`, createWillLodgementConfig2[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_email`)]]);

    I.click(createWillLodgementConfig2.continueButton);
};
