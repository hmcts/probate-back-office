'use strict';
const testConfig = require('src/test/config.js');

// Case worker - nav back to case
module.exports = async function (caseRef) {
    const I = this;
   
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement({xpath: '//select[@id="wb-case-type"]/option[text()="Grant of representation"]'});
    const url = `${testConfig.TestBackOfficeUrl}/cases/case-details/${await I.replaceAll(caseRef, '-', '')}`;
    await I.amOnLoadedPage(url);

    let numHomEls = 0;
    let loopCount = 0;
    while (numHomEls == 0 || loopCount > 20) {
        numHomEls = await I.grabNumberOfVisibleElements('exui-case-home');
        if (numHomEls === 0) {
            await I.wait(0.25);
            await I.amOnLoadedPage(url);
            loopCount++;
        }
    }
    
    await I.wait(testConfig.CaseworkerCaseNavigateDelay);
};
