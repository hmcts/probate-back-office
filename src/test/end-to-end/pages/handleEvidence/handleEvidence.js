'use strict';

const testConfig = require('src/test/config.js');
const handleEvidenceConfig = require('./handleEvidenceConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {
    const I = this;
    await I.waitForText(handleEvidenceConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);

    await I.click(`#evidenceHandled-${handleEvidenceConfig.checkbox}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
