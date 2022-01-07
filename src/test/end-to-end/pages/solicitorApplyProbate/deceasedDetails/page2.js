'use strict';

const deceasedDetailsConfig = require('./deceasedDetailsConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (applicationType, iHTFormsCompleted, whichIHTFormsCompleted) {
    const I = this;

    //await I.waitForElement('#deceasedDomicileInEngWales');
    await I.runAccessibilityTest();

    if (applicationType === 'EE') {
        if(iHTFormsCompleted === 'Yes'){
            await I.click(`#ihtFormEstateValuesCompleted_${deceasedDetailsConfig.optionYes}`);
            await I.waitForText(deceasedDetailsConfig.page2_whichIHTFormsLabel);
            await I.waitForText(deceasedDetailsConfig.page2_IHT207Label);
            await I.waitForText(deceasedDetailsConfig.page2_IHT400421Label);

            if(whichIHTFormsCompleted === 'IHT207'){
                //await I.click(`#page2_IHTOption`);
                await I.click({css: `#ihtFormEstate-${deceasedDetailsConfig.page2_IHTOptionEE207}`});
            }
            else{
                await I.click({css: `#ihtFormEstate-${deceasedDetailsConfig.page2_IHTOptionEE400421}`});
            }
        }
        else {
            await I.click(`#ihtFormEstateValuesCompleted_${deceasedDetailsConfig.optionNo}`);
            await I.waitForText(deceasedDetailsConfig.page2_grossValueIHTEstateLabel);
            await I.waitForText(deceasedDetailsConfig.page2_netValueIHTEstateLabel);
            await I.waitForText(deceasedDetailsConfig.page2_netQualifyingValueIHTEstateLabel);

            await I.fillField('#ihtEstateGrossValue', deceasedDetailsConfig.page2_grossValueIHTEstate);
            await I.fillField('#ihtEstateNetValue', deceasedDetailsConfig.page2_netValueIHTEstate);
            await I.fillField('#ihtEstateNetQualifyingValue', deceasedDetailsConfig.page2_netQualifyingValueIHTEstate);

            await I.click(`#deceasedHadLateSpouseOrCivilPartner_${deceasedDetailsConfig.optionYes}`);
            await I.click(`#ihtUnusedAllowanceClaimed_${deceasedDetailsConfig.optionYes}`);
        }
    }
    else if(applicationType === 'MultiExec'){
        await I.click({css: `#ihtFormId-${deceasedDetailsConfig.page2_IHTOptionMulti}`});
        await I.waitForText(deceasedDetailsConfig.page2_NilRateBandLabel);
        await I.click({css: `#iht217_${deceasedDetailsConfig.optionYes}`});
    }
    else{
        await I.click({css: `#ihtFormId-${deceasedDetailsConfig.page2_IHTOption}`});
    }


    //await I.click(`#deceasedDomicileInEngWales_${deceasedDetailsConfig.optionYes}`);
    //await I.click(`#deceasedAnyOtherNames_${deceasedDetailsConfig.optionNo}`);

    //await I.click(deceasedDetailsConfig.UKpostcodeLink);

    /*await I.fillField('#deceasedAddress__detailAddressLine1', deceasedDetailsConfig.address_line1);
    await I.fillField('#deceasedAddress__detailAddressLine2', deceasedDetailsConfig.address_line2);
    await I.fillField('#deceasedAddress__detailAddressLine3', deceasedDetailsConfig.address_line3);
    await I.fillField('#deceasedAddress__detailPostTown', deceasedDetailsConfig.address_town);
    await I.fillField('#deceasedAddress__detailCounty', deceasedDetailsConfig.address_county);
    await I.fillField('#deceasedAddress__detailPostCode', deceasedDetailsConfig.address_postcode);
    await I.fillField('#deceasedAddress__detailCountry', deceasedDetailsConfig.address_country);*/

    /*await I.waitForText(deceasedDetailsConfig.page2_solsIHT205FormLabel);
    await I.waitForText(deceasedDetailsConfig.page2_solsIHT207FormLabel);
    await I.waitForText(deceasedDetailsConfig.page2_solsIHT400421FormLabel);
    await I.waitForText(deceasedDetailsConfig.page2_solsIHTDNUFormLabel);
    await I.click({css: `#ihtFormId-${deceasedDetailsConfig.page2_IHTOption}`});*/
    await I.fillField('#ihtGrossValue', deceasedDetailsConfig.page2_ihtGrossValue);
    await I.fillField('#ihtNetValue', deceasedDetailsConfig.page2_ihtNetValue);

    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
