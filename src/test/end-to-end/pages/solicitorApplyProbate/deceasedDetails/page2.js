'use strict';

const deceasedDetailsConfig = require('./deceasedDetailsConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (applicationType, iHTFormsCompleted, whichIHTFormsCompleted) {
    const I = this;
    await I.runAccessibilityTest();

    if (applicationType === 'EE') {
        if (iHTFormsCompleted === 'Yes') {
            await I.click(`#ihtFormEstateValuesCompleted_${deceasedDetailsConfig.optionYes}`);
            await I.waitForText(deceasedDetailsConfig.page2_whichIHTFormsLabel);
            await I.waitForText(deceasedDetailsConfig.page2_IHT207Label);
            await I.waitForText(deceasedDetailsConfig.page2_IHT400421Label);
            await I.waitForText(deceasedDetailsConfig.page2_IHT400Label);

            if (whichIHTFormsCompleted === 'IHT207') {
                await I.click({css: `#ihtFormEstate-${deceasedDetailsConfig.page2_IHTOptionEE207}`});
            } else if (whichIHTFormsCompleted === 'IHT400') {
                await I.click({css: `#ihtFormEstate-${deceasedDetailsConfig.page2_IHTOptionEE400}`});
            } else {
                await I.click({css: `#ihtFormEstate-${deceasedDetailsConfig.page2_IHTOptionEE400421}`});
            }

        } else {
            await I.click(`#ihtFormEstateValuesCompleted_${deceasedDetailsConfig.optionNo}`);
            await I.waitForText(deceasedDetailsConfig.page2_grossValueIHTEstateLabel);
            await I.waitForText(deceasedDetailsConfig.page2_netValueIHTEstateLabel);
            await I.waitForText(deceasedDetailsConfig.page2_netQualifyingValueIHTEstateLabel);

            await I.fillField('#ihtEstateGrossValue', deceasedDetailsConfig.page2_grossValueIHTEstate);
            await I.fillField('#ihtEstateNetValue', deceasedDetailsConfig.page2_netValueIHTEstate);
            await I.fillField('#ihtEstateNetQualifyingValue', deceasedDetailsConfig.page2_netQualifyingValueIHTEstate);
            await I.waitForNavigationToComplete(commonConfig.continueButton, true);

            await I.click(`#deceasedHadLateSpouseOrCivilPartner_${deceasedDetailsConfig.optionYes}`);
            await I.click(`#ihtUnusedAllowanceClaimed_${deceasedDetailsConfig.optionYes}`);
        }
    } else if (applicationType === 'MultiExec') {
        await I.click({css: `#ihtFormId-${deceasedDetailsConfig.page2_IHTOptionMulti}`});
        await I.waitForText(deceasedDetailsConfig.page2_NilRateBandLabel);
        await I.click({css: `#iht217_${deceasedDetailsConfig.optionYes}`});
    } else {
        await I.click({css: `#ihtFormId-${deceasedDetailsConfig.page2_IHTOption}`});
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
