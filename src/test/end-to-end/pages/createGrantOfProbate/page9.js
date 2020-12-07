'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {

    const I = this;

    await I.waitForText(createGrantOfProbateConfig.page9_waitForText, testConfig.TestTimeToWaitForText);
    await I.click(`#ihtFormCompletedOnline-${createGrantOfProbateConfig.page9_ihtFormCompletedOnlineYes}`);
    await I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page9_ihtReferenceNumber);
    await I.fillField('#ihtGrossValue', createGrantOfProbateConfig.page9_ihtGrossValue);
    await I.fillField('#ihtNetValue', createGrantOfProbateConfig.page9_ihtNetValue);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
