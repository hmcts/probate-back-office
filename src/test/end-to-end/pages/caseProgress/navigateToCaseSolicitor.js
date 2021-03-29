'use strict';
const testConfig = require('src/test/config.js');

// Solicitor - navigate back to case
module.exports = async function (caseRef, optTextToCheck) {
    const I = this;
    await I.waitForElement({css: '#wb-case-state'});
    if (optTextToCheck) {
        await I.waitForElement({xpath: `//select/option[text()="${optTextToCheck}"]`});
    }
    await I.amOnPage(`${testConfig.TestBackOfficeUrl}/v2/case/${await I.replaceAll(caseRef, '-', '')}`);
};
