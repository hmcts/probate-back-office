'use strict';
const testConfig = require('src/test/config.js');

// Case worker - nav back to case
module.exports = async function (caseRef) {
    const I = this;

    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement({xpath: '//select[@id="wb-case-type"]/option[text()="Grant of representation"]'});
    await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/${await I.replaceAll(caseRef, '-', '')}`);
    if (testConfig.TestForXUI) {
        await I.wait(2);
    }
};
