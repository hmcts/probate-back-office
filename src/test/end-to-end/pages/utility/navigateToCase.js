'use strict';
const testConfig = require('src/test/config.js');

// Case worker - nav back to case
module.exports = async function (caseRef) {
    const I = this;

    const scenarioName = 'Find cases';
    await I.logInfo(scenarioName, 'Navigating to case');
    await I.waitForElement({xpath: '//select[@id="wb-case-type"]/option[text()="Grant of representation"]'});
    const searchLinkLocator = {css: 'a[href="/cases/case-search"]:first-child'};
    await I.waitForVisible(searchLinkLocator);
    await I.waitForClickable(searchLinkLocator);

    /*
    // now that waitforNavigation has networkidle2 wait shouldn't need this, but retained for pipeline (autodelay true)
    await I.logInfo(scenarioName, 'About to click search link');
    await I.wait(testConfig.FindCasesDelay);
    await I.click(searchLinkLocator);
    await I.logInfo(scenarioName, 'Search link clicked, now waiting for case ref input field to be visible and enabled');

    const caseRefLocator = {css: 'input[id="[CASE_REFERENCE]"]'};
    await I.waitForVisible(caseRefLocator);
    await I.waitForEnabled(caseRefLocator);
    await I.logInfo(scenarioName, 'case ref input field now visible and enabled');

    // await I.waitForElement({css: 'exui-search-case'});
    await I.waitForEnabled({css: '#s-jurisdiction'});
    await I.waitForElement({css: '#s-jurisdiction option'});
    await I.waitForEnabled({css: '#s-case-type'});
    await I.waitForElement({css: '#s-case-type option'});
    await I.selectOption({css: '#s-case-type'}, 'Grant of representation');
    await I.waitForVisible(caseRefLocator);
    await I.waitForEnabled(caseRefLocator);
    await I.fillField(caseRefLocator, caseRef);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // implicit wait needed here
    }
    await I.waitForEnabled({css: 'button[title="Apply filter"]'});
    await I.click({css: 'button[title="Apply filter"]'});

    const caseRefNoDashes = await I.replaceAll(caseRef, '-', '');
    const linkLocator = {css: `a.govuk-link[href="/cases/case-details/${caseRefNoDashes}"]`};

    await I.waitForVisible(linkLocator);
    // now that waitforNavigation has networkidle2 wait shouldn't need this, but retained for pipeline (autodelay true)
    await I.wait(testConfig.FindCasesDelay);
    await I.waitForNavigationToComplete(linkLocator.css);
    */

    const url = `${testConfig.TestBackOfficeUrl}/cases/case-details/${await I.replaceAll(caseRef, '-', '')}`;
    await I.amOnLoadedPage(url);
    
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
