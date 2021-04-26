'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {

    const I = this;

    await I.waitForText(createGrantOfProbateConfig.page10_waitForText, testConfig.TestTimeToWaitForText);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.5);
    }
    await I.click(`#ihtFormCompletedOnline-${createGrantOfProbateConfig.page10_ihtFormCompletedOnlineYes}`);
    await I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page10_ihtReferenceNumber);
    await I.fillField('#ihtGrossValue', createGrantOfProbateConfig.page10_ihtGrossValue);
    await I.fillField('#ihtNetValue', createGrantOfProbateConfig.page10_ihtNetValue);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
