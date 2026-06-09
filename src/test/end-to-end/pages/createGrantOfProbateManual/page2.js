'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateManualConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStandard/caseProgressConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.EE_waitForText, testConfig.WaitForTextTimeout);
        await I.click(`#ihtFormEstateValuesCompleted_${createGrantOfProbateConfig.EE_ihtFormEstateValueCompletedNo}`);

        await I.fillField('#ihtEstateGrossValue', createGrantOfProbateConfig.EE_ihtEstateGrossValue);
        await I.fillField('#ihtEstateNetValue', createGrantOfProbateConfig.EE_ihtEstateNetValue);
        await I.fillField('#ihtEstateNetQualifyingValue', createGrantOfProbateConfig.EE_ihtEstateNetValue);

        await I.click(`#deceasedHadLateSpouseOrCivilPartner_${createGrantOfProbateConfig.EE_deceasedHadLateSpouseOrCivilPartnerYes}`);
        await I.click(`#ihtUnusedAllowanceClaimed_${createGrantOfProbateConfig.EE_ihtUnusedAllowanceClaimed_No}`);
    } else {
        await I.waitForText(caseProgressConfig.IHT205Label);
        await I.waitForText(caseProgressConfig.IHT400Label);
        await I.click({css: `#ihtFormId-${caseProgressConfig.IHT400Option}`});
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
