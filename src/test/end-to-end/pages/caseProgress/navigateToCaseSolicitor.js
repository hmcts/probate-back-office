'use strict';
const testConfig = require('src/test/config.js');

// Solicitor - navigate back to case
module.exports = async function (caseRef) {
    const I = this;
    await I.waitForElement({css: '#wb-case-state'});
    await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/${await I.replaceAll(caseRef, '-', '')}`);
    if (testConfig.TestForXUI) {
        await I.wait(2);
    }

};
