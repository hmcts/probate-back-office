'use strict';

const testConfig = require('src/test/config.js');
const handleEvidenceConfig = require('./handleEvidenceConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {
    const I = this;
    await I.waitForText(handleEvidenceConfig.waitForText, testConfig.WaitForTextTimeout);

    await I.see(caseRef);

    await I.waitForEnabled({css: `#evidenceHandled_${handleEvidenceConfig.checkbox}`});
    await I.click({css: `#evidenceHandled_${handleEvidenceConfig.checkbox}`});

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
