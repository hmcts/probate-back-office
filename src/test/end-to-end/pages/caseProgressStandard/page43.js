'use strict';
const testConfig = require('src/test/config.js');

// Solicitor - navigate back to case
module.exports = async function (caseRef) {
    const I = this;
    // make sure solicitor can see this state
    const optText = 'Examining'; 
    await I.waitForElement({xpath: `//select/option[text()="${optText}"]`});

    await I.amOnPage(`${testConfig.TestBackOfficeUrl}/v2/case/${await I.replaceAll(caseRef,'-','')}`);
    await I.waitForNavigationToComplete();
};
