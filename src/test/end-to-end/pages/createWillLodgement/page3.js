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
        // const result = value.filter(word => word.toLowerCase().indexOf(`page3_executor${counter}`.toLowerCase()) > -1);
        if (value.includes(`page3_executor${index}`)) {
            executorFieldList.push(value);
        }
    });

    if (crud === 'create') {
        await I.waitForText(createWillLodgementConfig.page3_waitForText, testConfig.WaitForTextTimeout);

        await I.fillField('#executorTitle', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title`)]]);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.fillField('#executorForenames', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_forenames`)]]);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.fillField('#executorSurname', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_surname`)]]);
        await I.fillField('#executorEmailAddress', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email`)]]);

        await I.click(createWillLodgementConfig.UKpostcodeLink);

        await I.fillField('#executorAddress__detailAddressLine1', createWillLodgementConfig.address_line1);
        await I.fillField('#executorAddress__detailAddressLine2', createWillLodgementConfig.address_line2);
        await I.fillField('#executorAddress__detailAddressLine3', createWillLodgementConfig.address_line3);
        await I.fillField('#executorAddress__detailPostTown', createWillLodgementConfig.address_town);
        await I.fillField('#executorAddress__detailCounty', createWillLodgementConfig.address_county);
        await I.fillField('#executorAddress__detailPostCode', createWillLodgementConfig.address_postcode);
        await I.fillField('#executorAddress__detailCountry', createWillLodgementConfig.address_country);

        Object.keys(createWillLodgementConfig).forEach(function (value) {
            if (value.includes('page3_additional_executor')) {
                additionalExecutorFieldList.push(value);
            }
        });

        await I.click(createWillLodgementConfig.page3_addExecutorButton);

        await I.waitForEnabled({css: `#additionalExecutorList_${index}_executorForenames`});
        await I.wait(testConfig.ManualDelayMedium); // webdriver having problems here

        await I.fillField(`#additionalExecutorList_${index}_executorTitle`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_title`)]]);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.fillField(`#additionalExecutorList_${index}_executorForenames`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_forenames`)]]);
        await I.fillField(`#additionalExecutorList_${index}_executorSurname`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_surname`)]]);

        await I.click(createWillLodgementConfig.UKpostcodeLink);

        await I.waitForVisible(`#additionalExecutorList_${index}_executorAddress__detailAddressLine1`);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress__detailAddressLine1`, createWillLodgementConfig.address_line1);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress__detailAddressLine2`, createWillLodgementConfig.address_line2);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress__detailAddressLine3`, createWillLodgementConfig.address_line3);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress__detailPostTown`, createWillLodgementConfig.address_town);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress__detailCounty`, createWillLodgementConfig.address_county);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress__detailPostCode`, createWillLodgementConfig.address_postcode);
        await I.fillField(`#additionalExecutorList_${index}_executorAddress__detailCountry`, createWillLodgementConfig.address_country);

        await I.fillField(`#additionalExecutorList_${index}_executorEmailAddress`, createWillLodgementConfig[additionalExecutorFieldList[additionalExecutorFieldList.indexOf(`page3_additional_executor${index}_email`)]]);
    }

    if (crud === 'update') {
        await I.waitForText(createWillLodgementConfig.page3_amend_waitForText, testConfig.WaitForTextTimeout);

        await I.fillField('#executorTitle', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_title_update`)]]);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayMedium);
        }
        await I.fillField('#executorForenames', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_forenames_update`)]]);
        if (!testConfig.TestAutoDelayEnabled) {
            // only valid for local dev where we need it to run as fast as poss to minimise
            // lost dev time
            await I.wait(testConfig.ManualDelayShort);
        }
        await I.fillField('#executorSurname', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_surname_update`)]]);
        await I.fillField('#executorEmailAddress', createWillLodgementConfig[executorFieldList[executorFieldList.indexOf(`page3_executor${index}_email_update`)]]);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
