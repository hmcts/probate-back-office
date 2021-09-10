'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {

    const I = this;

    await I.waitForText(createGrantOfProbateConfig.page9_waitForText, testConfig.WaitForTextTimeout);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort);
    }
    await I.waitForEnabled(`#ihtFormCompletedOnline_${createGrantOfProbateConfig.page9_ihtFormCompletedOnlineYes}`);
    await I.click(`#ihtFormCompletedOnline_${createGrantOfProbateConfig.page9_ihtFormCompletedOnlineYes}`);
    await I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page9_ihtReferenceNumber);
    await I.fillField('#ihtGrossValue', createGrantOfProbateConfig.page9_ihtGrossValue);
    await I.fillField('#ihtNetValue', createGrantOfProbateConfig.page9_ihtNetValue);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
