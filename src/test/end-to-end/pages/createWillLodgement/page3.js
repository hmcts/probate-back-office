'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig = require('./createWillLodgementConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

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
        await I.waitForText(createWillLodgementConfig.page3_waitForText, testConfig.TestTimeToWaitForText);

        await I.fillField('#executorTitle', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title`)]]);
        await I.fillField('#executorForenames', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_forenames`)]]);
        await I.fillField('#executorSurname', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_surname`)]]);
        await I.fillField('#executorEmailAddress', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email`)]]);

        await I.click(createWillLodgementConfig.UKpostcodeLink);

        await I.fillField('#executorAddress_AddressLine1', createWillLodgementConfig.address_line1);
        await I.fillField('#executorAddress_AddressLine2', createWillLodgementConfig.address_line2);
        await I.fillField('#executorAddress_AddressLine3', createWillLodgementConfig.address_line3);
        await I.fillField('#executorAddress_PostTown', createWillLodgementConfig.address_town);
        await I.fillField('#executorAddress_County', createWillLodgementConfig.address_county);
        await I.fillField('#executorAddress_PostCode', createWillLodgementConfig.address_postcode);
        await I.fillField('#executorAddress_Country', createWillLodgementConfig.address_country);

        Object.keys(createWillLodgementConfig).forEach(function (value) {
            if (value.includes('page3_additional_executor')) {
                additionalExecutorFieldList.push(value);
            }
        });

        await I.click(createWillLodgementConfig.page3_addExecutorButton);

        await I.waitForEnabled({css: `#additionalExecutorList_${index}_executorForenames`});
        await I.wait(0.5); // webdriver having problems here

        await I.fillField(`#additionalExecutorList_${index}_executorTitle`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_title`)]]);
        await I.fillField(`#additionalExecutorList_${index}_executorForenames`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_forenames`)]]);
        await I.fillField(`#additionalExecutorList_${index}_executorSurname`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_surname`)]]);

        await I.click(createWillLodgementConfig.UKpostcodeLink);

        await I.waitForVisible(`#additionalExecutorList_${index}_executorAddress_AddressLine1`);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress_AddressLine1`, createWillLodgementConfig.address_line1);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress_AddressLine2`, createWillLodgementConfig.address_line2);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress_AddressLine3`, createWillLodgementConfig.address_line3);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress_PostTown`, createWillLodgementConfig.address_town);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress_County`, createWillLodgementConfig.address_county);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress_PostCode`, createWillLodgementConfig.address_postcode);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress_Country`, createWillLodgementConfig.address_country);

        await I.fillField(`#additionalExecutorList_${index}_executorEmailAddress`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_email`)]]);
    }

    if (crud === 'update') {
        await I.waitForText(createWillLodgementConfig.page3_amend_waitForText, testConfig.TestTimeToWaitForText);

        await I.fillField('#executorTitle', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title_update`)]]);
        await I.fillField('#executorForenames', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_forenames_update`)]]);
        await I.fillField('#executorSurname', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_surname_update`)]]);
        await I.fillField('#executorEmailAddress', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email_update`)]]);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
