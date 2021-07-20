'use strict';
const caseProgressConfig = require('./caseProgressConfig');

module.exports = async function () {
    const I = this;
    // If this hangs, then case progress tab has not been generated / not been generated correctly and test fails.
    // Make sure app stopped text is shown as inset
    await I.waitForText(caseProgressConfig.AppStoppedTabTitle, 2, {css: 'div.govuk-inset-text'});

    await I.waitForText(caseProgressConfig.AppStoppedTabCheckText);

    await I.signOut();
};
