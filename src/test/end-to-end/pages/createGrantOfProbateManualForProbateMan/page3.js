'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateManualProbateManCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(createGrantOfProbateConfig.page3_waitForText, testConfig.WaitForTextTimeout);

        await I.fillField('#ihtGrossValue', createGrantOfProbateConfig.IhtGrossValue);
        await I.fillField('#ihtNetValue', createGrantOfProbateConfig.IhtNetValue);
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
