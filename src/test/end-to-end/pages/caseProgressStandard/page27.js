'use strict';
const testConfig = require('src/test/config.js');

// Solicitor - navigate back to case
module.exports = async function (caseRef) {
    const I = this;
    await I.waitForElement({css: '#wb-case-state'});

    await I.amOnPage(`${testConfig.TestBackOfficeUrl}/v2/case/${await I.replaceAll(caseRef,'-','')}`);
    await I.waitForNavigationToComplete();
};
