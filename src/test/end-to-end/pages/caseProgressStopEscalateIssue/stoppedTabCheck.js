'use strict';
const moment = require('moment');

// Solicitor - navigate back to case
module.exports = async function () {
    const I = this;
    // If this hangs, then case progress tab has not been generated / not been generated correctly and test fails.
    // Make sure case stopped text is shown as inset
    await I.waitForText('Case stopped', 2, {css: 'div.govuk-inset-text'});

    // Check date format

    await I.waitForText(`The case was stopped on ${moment().format('DD MMM yyyy')} for one of two reasons:`);

    await I.waitForNavigationToComplete('#sign-out');
};
