'use strict';

const testConfig = require('src/test/config');
const createWillLodgementConfig2 = require('./createWillLodgementFastConfig2');

module.exports = function () {

    const I = this;

    I.waitForText(createWillLodgementConfig2.page3_waitForText, testConfig.TestTimeToWaitForText);
    //  I.amOnPage(createWillLodgementConfig2.pageUrl);

    let index = 0;
    let counter = 1;

    Object.keys(createWillLodgementConfig2).forEach( function (value) {
        //const result = value.filter(word => word.toLowerCase().indexOf(`page3_executor${counter}`.toLowerCase()) > -1);
        if (value.includes(`page3_executor${index}`)) {

            console.log('value>>>', value);

            switch (counter) {
                case 1:
                    I.fillField('#executorTitle', createWillLodgementConfig2[value]);
                    break;
                case 2:
                    I.fillField('#executorForenames', createWillLodgementConfig2[value]);
                    break;
                case 3:
                    I.fillField('#executorSurname', createWillLodgementConfig2[value]);
                    break;
                case 4:
                    I.fillField('#executorEmailAddress', createWillLodgementConfig2[value]);
                    break;
            }

            counter += 1;

        }
    });

    if (index === 0) {
        I.click(createWillLodgementConfig2.UKpostcodeLink);
        I.fillField('#AddressLine1', createWillLodgementConfig2.address_line1);
        I.fillField('#AddressLine2', createWillLodgementConfig2.address_line2);
        I.fillField('#AddressLine3', createWillLodgementConfig2.address_line3);
        I.fillField('#PostTown', createWillLodgementConfig2.address_town);
        I.fillField('#County', createWillLodgementConfig2.address_county);
        I.fillField('#PostCode', createWillLodgementConfig2.address_postcode);
        I.fillField('#Country', createWillLodgementConfig2.address_country);
    }

    // createWillLodgementConfig2.page3.executorList.forEach(function(value,key) {
    //     if (key === 0) {
    //         I.fillField('#executorTitle', value.title);
    //         I.fillField('#executorForenames', value.firstnames);
    //         I.fillField('#executorSurname', value.lastnames);
    //
    //         I.click(createWillLodgementConfig2.common.enterUKpostcodeLink);
    //         I.fillField('#AddressLine1', createWillLodgementConfig2.page3.common.address.line1);
    //         I.fillField('#AddressLine2', createWillLodgementConfig2.page3.common.address.line2);
    //         I.fillField('#AddressLine3', createWillLodgementConfig2.page3.common.address.line3);
    //         I.fillField('#PostTown', createWillLodgementConfig2.page3.common.address.town);
    //         I.fillField('#County', createWillLodgementConfig2.page3.common.address.county);
    //         I.fillField('#PostCode', createWillLodgementConfig2.page3.common.address.postcode);
    //         I.fillField('#Country', createWillLodgementConfig2.page3.common.address.country);
    //         I.fillField('#executorEmailAddress', value.email);
    //     } else {
    //         let index = parseInt(key) - 1;
    //         I.click(createWillLodgementConfig2.page3.common.addAnotherExecutor);
    //
    //         I.fillField(`#additionalExecutorList_${index}_executorTitle`, value.title);
    //         I.fillField(`#additionalExecutorList_${index}_executorForenames`, value.firstnames);
    //         I.fillField(`#additionalExecutorList_${index}_executorSurname`, value.lastnames);
    //
    //         I.click(createWillLodgementConfig2.common.enterUKpostcodeLink);
    //
    //         I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine1`, createWillLodgementConfig2.page3.common.address.line1);
    //         I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine2`, createWillLodgementConfig2.page3.common.address.line2);
    //         I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #AddressLine3`, createWillLodgementConfig2.page3.common.address.line3);
    //         I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #PostTown`, createWillLodgementConfig2.page3.common.address.town);
    //         I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #County`, createWillLodgementConfig2.page3.common.address.county);
    //         I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #PostCode`, createWillLodgementConfig2.page3.common.address.postcode);
    //         I.fillField(`#additionalExecutorList_${index}_executorAddress_executorAddress #executorAddress #Country`, createWillLodgementConfig2.page3.common.address.country);
    //         I.fillField(`#additionalExecutorList_${index}_executorEmailAddress`, value.email);
    //     }
    //     //            additionalExecutorList_0_executorTitle
    // })
        I.click(createWillLodgementConfig2.common.locator);
};
