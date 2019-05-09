'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function () {

    const I = this;

    I.waitForText(createGrantOfProbateConfig.page9_waitForText, testConfig.TestTimeToWaitForText);
    I.click(`#ihtFormCompletedOnline-${createGrantOfProbateConfig.page9_ihtFormCompletedOnlineYes}`);
    I.fillField('#ihtReferenceNumber', createGrantOfProbateConfig.page9_ihtReferenceNumber);
    I.fillField('#ihtGrossValue', createGrantOfProbateConfig.page9_ihtGrossValue);
    I.fillField('#ihtNetValue', createGrantOfProbateConfig.page9_ihtNetValue);

    I.waitForNavigationToComplete(commonConfig.continueButton);
};
