'use strict';
const testConfig = require('src/test/config.js');

// Case worker - nav back to case
module.exports = async function (caseType, caseRef, useWaitInUrl = true) {
    const I = this;

    const scenarioName = 'Find cases';
    await I.logInfo(scenarioName, 'Navigating to case');

    const caseRefNoDashes = await I.replaceAll(caseRef, '-', '');
    const caseTypeNoSpace = await I.replaceAll(caseType,' ','');
    if (useWaitInUrl) {
        I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/PROBATE/${caseTypeNoSpace}/${caseRefNoDashes}`);
    } else {
        I.amOnPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/PROBATE/${caseTypeNoSpace}/${caseRefNoDashes}`);
    }

    await I.wait(testConfig.ManualDelayMedium);
    await I.rejectCookies();
};
