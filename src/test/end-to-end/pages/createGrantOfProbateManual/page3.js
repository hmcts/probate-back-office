'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateManualConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page3_waitForText, testConfig.WaitForTextTimeout);

        await I.fillField('#ihtGrossValue', createGrantOfProbateConfig.EE_ihtEstateGrossValue);
        await I.fillField('#ihtNetValue', createGrantOfProbateConfig.EE_ihtEstateNetValue);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
