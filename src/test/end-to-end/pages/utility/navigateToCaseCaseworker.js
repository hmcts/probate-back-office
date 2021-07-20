'use strict';
const testConfig = require('src/test/config.js');

// Case worker - nav back to case
module.exports = async function (caseRef) {
    const I = this;

    await I.waitForElement({xpath: '//select[@id="wb-case-type"]/option[text()="Grant of representation"]'});
    const searchLinkLocator = {css: 'a[href="/cases/case-search"]'};
    await I.waitForVisible(searchLinkLocator);
    // apply a small wait to allow for javascript on page to load and wire up the link
    await I.wait(testConfig.FindCasesDelay);    
    await I.click(searchLinkLocator);
    await I.waitForElement({css: 'exui-search-case'});
    await I.waitForEnabled({css: '#s-jurisdiction'});
    await I.waitForElement({css: '#s-jurisdiction option'});
    await I.waitForEnabled({css: '#s-case-type'});
    await I.waitForElement({css: '#s-case-type option'});
    await I.selectOption({css: '#s-case-type'}, 'Grant of representation');
    await I.waitForEnabled({css: 'input[id="[CASE_REFERENCE]"]'});
    await I.fillField({css: 'input[id="[CASE_REFERENCE]'}, caseRef);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // implicit wait needed here
    }
    await I.waitForEnabled({css: 'button[title="Apply filter"]'});
    await I.click({css: 'button[title="Apply filter"]'});

    const caseRefNoDashes = await I.replaceAll(caseRef, '-', '');
    const linkLocator = {css: `a.govuk-link[href="/cases/case-details/${caseRefNoDashes}"]`};
   
    await I.waitForVisible(linkLocator);
    // apply a small wait to allow for javascript on page to load and wire up the link
    await I.wait(testConfig.FindCasesDelay);
    await I.waitForNavigationToComplete(linkLocator.css);

    /*
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
    */

    await I.wait(testConfig.CaseworkerCaseNavigateDelay);
};
