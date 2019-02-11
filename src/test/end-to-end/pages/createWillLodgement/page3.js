'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig = require('./createWillLodgementConfig');

module.exports = function () {

    const I = this;

    I.waitForText(createWillLodgementConfig.page3_waitForText, testConfig.TestTimeToWaitForText);

    const index = 0;
    let executorFieldList = [];
    let additionalExecutorFieldList = [];

    Object.keys(createWillLodgementConfig).forEach(function (value) {
        //const result = value.filter(word => word.toLowerCase().indexOf(`page3_executor${counter}`.toLowerCase()) > -1);
        if (value.includes(`page3_executor${index}`)) {
            executorFieldList.push(value);
        }
    });

    I.fillField('#executorTitle', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title`)]]);
    I.fillField('#executorForenames', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_firstnames`)]]);
    I.fillField('#executorSurname', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_lastnames`)]]);
    I.fillField('#executorEmailAddress', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email`)]]);

    I.click(createWillLodgementConfig.UKpostcodeLink);
    I.fillField('#AddressLine1', createWillLodgementConfig.address_line1);
    I.fillField('#AddressLine2', createWillLodgementConfig.address_line2);
    I.fillField('#AddressLine3', createWillLodgementConfig.address_line3);
    I.fillField('#PostTown', createWillLodgementConfig.address_town);
    I.fillField('#County', createWillLodgementConfig.address_county);
    I.fillField('#PostCode', createWillLodgementConfig.address_postcode);
    I.fillField('#Country', createWillLodgementConfig.address_country);

    Object.keys(createWillLodgementConfig).forEach(function (value) {
        if (value.includes('page3_additional_executor')) {
            additionalExecutorFieldList.push(value);
        }
    });

    I.click(createWillLodgementConfig.page3_addExecutorButton);

    I.fillField(`#additionalExecutorList_${index}_executorTitle`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_title`)]]);
    I.fillField(`#additionalExecutorList_${index}_executorForenames`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_firstnames`)]]);
    I.fillField(`#additionalExecutorList_${index}_executorSurname`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_lastnames`)]]);

    I.click(createWillLodgementConfig.UKpostcodeLink);

    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine1`, createWillLodgementConfig.address_line1);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine2`, createWillLodgementConfig.address_line2);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine3`, createWillLodgementConfig.address_line3);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #PostTown`, createWillLodgementConfig.address_town);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #County`, createWillLodgementConfig.address_county);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #PostCode`, createWillLodgementConfig.address_postcode);
    I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #Country`, createWillLodgementConfig.address_country);

    I.fillField(`#additionalExecutorList_${index}_executorEmailAddress`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_email`)]]);

    I.click(createWillLodgementConfig.continueButton);
};
