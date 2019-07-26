'use strict';

const testConfig = require('src/test/config.js');
const handleEvidenceConfig = require('./handleEvidenceConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(handleEvidenceConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.click(`#evidenceHandled-${handleEvidenceConfig.checkbox}`);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
