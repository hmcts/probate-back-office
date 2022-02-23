'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateManualConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.EE_waitForText, testConfig.WaitForTextTimeout);
        await I.click(`#ihtFormEstateValuesCompleted_${createGrantOfProbateConfig.EE_ihtFormEstateValueCompletedNo}`);

        await I.fillField('#ihtEstateGrossValue', createGrantOfProbateConfig.EE_ihtEstateGrossValue);
        await I.fillField('#ihtEstateNetValue', createGrantOfProbateConfig.EE_ihtEstateNetValue);
        await I.fillField('#ihtEstateNetQualifyingValue', createGrantOfProbateConfig.EE_ihtEstateNetValue);

        await I.click(`#deceasedHadLateSpouseOrCivilPartner_${createGrantOfProbateConfig.EE_deceasedHadLateSpouseOrCivilPartnerNo}`);
        await I.click(`#ihtUnusedAllowanceClaimed_${createGrantOfProbateConfig.EE_ihtUnusedAllowanceClaimed_No}`);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
