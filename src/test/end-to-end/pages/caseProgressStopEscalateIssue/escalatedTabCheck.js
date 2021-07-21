'use strict';
const moment = require('moment');
const testConfig = require('src/test/config');

// Solicitor - navigate back to case
module.exports = async function () {
    const I = this;

    // If this hangs, then case progress tab has not been generated / not been generated correctly and test fails.
    // Make sure case stopped text is shown as inset
    await I.waitForText('Case escalated to a Registrar', testConfig.WaitForTextTimeout || 60);

    // Check date format

    await I.waitForText(`The case was escalated on ${moment().format('DD MMM yyyy')}.`);

    await I.signOut();
};
