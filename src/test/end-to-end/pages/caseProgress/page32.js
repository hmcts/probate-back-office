'use strict';
const moment = require('moment');

const testConfig = require('src/test/config.js');

// Solicitor - navigate back to case
module.exports = async function (caseRef) {
    const I = this;
    
    // If this hangs, then case progress tab has not been generated / not been generated correctly and test fails.

    // Make sure case stopped text is shown as inset
    await I.waitForText('Case stopped', 2, {css: 'div.govuk-inset-text'});

    // Check date format

    await I.waitForText(`The case was stopped on ${moment().format("DD MMM yyyy")} for one of two reasons:`, 2, {css: '#wb-case-state option'});

    // await I.amOnPage(`${testConfig.TestBackOfficeUrl}/v2/case/${await I.replaceAll(caseRef,'-','')}`);
    await I.waitForNavigationToComplete();

};
