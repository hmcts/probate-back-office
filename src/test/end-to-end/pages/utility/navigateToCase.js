'use strict';
const testConfig = require('src/test/config.js');

// Case worker - nav back to case
module.exports = async function (caseRef) {
    const I = this;
    const scenarioName = 'Find cases';
    await I.logInfo(scenarioName, 'Navigating to case');
    const caseRefNoDashes = await I.replaceAll(caseRef, '-', '');
    await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/${caseRefNoDashes}`);
    await I.wait(testConfig.ManualDelayMedium);
    await I.rejectCookies();
};
