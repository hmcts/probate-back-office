'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig = require('./createWillLodgementConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (crud) {

    const I = this;

    const index = 0;
    /* eslint prefer-const: 0 */
    let executorFieldList = [];
    let additionalExecutorFieldList = [];

    Object.keys(createWillLodgementConfig).forEach(function (value) {
        //const result = value.filter(word => word.toLowerCase().indexOf(`page3_executor${counter}`.toLowerCase()) > -1);
        if (value.includes(`page3_executor${index}`)) {
            executorFieldList.push(value);
        }
    });

    if (crud === 'create') {
        I.waitForText(createWillLodgementConfig.page3_waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#executorTitle', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title`)]]);
        I.fillField('#executorForenames', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_forenames`)]]);
        I.fillField('#executorSurname', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_surname`)]]);
        I.fillField('#executorEmailAddress', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email`)]]);

        I.click(createWillLodgementConfig.UKpostcodeLink);

        I.fillField('#executorAddress_AddressLine1', createWillLodgementConfig.address_line1);
        I.fillField('#executorAddress_AddressLine2', createWillLodgementConfig.address_line2);
        I.fillField('#executorAddress_AddressLine3', createWillLodgementConfig.address_line3);
        I.fillField('#executorAddress_PostTown', createWillLodgementConfig.address_town);
        I.fillField('#executorAddress_County', createWillLodgementConfig.address_county);
        I.fillField('#executorAddress_PostCode', createWillLodgementConfig.address_postcode);
        I.fillField('#executorAddress_Country', createWillLodgementConfig.address_country);

        Object.keys(createWillLodgementConfig).forEach(function (value) {
            if (value.includes('page3_additional_executor')) {
                additionalExecutorFieldList.push(value);
            }
        });

        I.click(createWillLodgementConfig.page3_addExecutorButton);

        I.fillField(`#additionalExecutorList_${index}_executorTitle`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_title`)]]);
        I.fillField(`#additionalExecutorList_${index}_executorForenames`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_forenames`)]]);
        I.fillField(`#additionalExecutorList_${index}_executorSurname`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_surname`)]]);

        I.click(createWillLodgementConfig.UKpostcodeLink);

        I.fillField(`#additionalExecutorList_${index}_executorAddress_AddressLine1`, createWillLodgementConfig.address_line1);
        I.fillField(`#additionalExecutorList_${index}_executorAddress_AddressLine2`, createWillLodgementConfig.address_line2);
        I.fillField(`#additionalExecutorList_${index}_executorAddress_AddressLine3`, createWillLodgementConfig.address_line3);
        I.fillField(`#additionalExecutorList_${index}_executorAddress_PostTown`, createWillLodgementConfig.address_town);
        I.fillField(`#additionalExecutorList_${index}_executorAddress_County`, createWillLodgementConfig.address_county);
        I.fillField(`#additionalExecutorList_${index}_executorAddress_PostCode`, createWillLodgementConfig.address_postcode);
        I.fillField(`#additionalExecutorList_${index}_executorAddress_Country`, createWillLodgementConfig.address_country);

        I.fillField(`#additionalExecutorList_${index}_executorEmailAddress`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_email`)]]);
    }

    if (crud === 'update') {
        I.waitForText(createWillLodgementConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);

        I.fillField('#executorTitle', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title_update`)]]);
        I.fillField('#executorForenames', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_forenames_update`)]]);
        I.fillField('#executorSurname', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_surname_update`)]]);
        I.fillField('#executorEmailAddress', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email_update`)]]);
    }

    I.click(commonConfig.continueButton);
};
