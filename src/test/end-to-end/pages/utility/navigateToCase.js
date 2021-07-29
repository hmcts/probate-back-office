'use strict';
const testConfig = require('src/test/config.js');

// Case worker - nav back to case
module.exports = async function (caseRef) {
    const I = this;

    const scenarioName = 'Find cases';
    await I.logInfo(scenarioName, 'Navigating to case');
    await I.logInfo(scenarioName, `Waiting for ${testConfig.FindCasesInitialDelay} seconds`);
    await I.wait(testConfig.FindCasesInitialDelay);

    await I.logInfo(scenarioName, 'Waiting for caseType dropdown (not used, but indicates page has at least partially loaded)');
    // await I.waitForElement({xpath: '//select[@id="wb-case-type"]/option[text()="Grant of representation"]'});
    await I.waitForElement({css: '#wb-case-type'});
    await I.logInfo(scenarioName, 'Case type dropdwon populated, if that didn\'t happen or took too long - address exui issues or clear down data');

    /*
    const html = await I.grabSource();
    await I.logInfo(scenarioName, html);
    */

    // const url = `${testConfig.TestBackOfficeUrl}/cases/case-details/${await I.replaceAll(caseRef, '-', '')}`;
    // await I.amOnLoadedPage(url);

    const searchLinkLocator = {css: 'a[href="/cases/case-search"]:first-child'};
    await I.logInfo(scenarioName, 'Waiting for case-search link');
    await I.waitForVisible(searchLinkLocator);
    await I.waitForElement(searchLinkLocator);

    // This code navigates to case by searching for the case by case ref in the UI, then clicking it.
    // Works fine locally but we have issues in the pipeline

    // now that waitforNavigation has networkidle2 wait shouldn't need this, but retained for pipeline (autodelay true)
    await I.logInfo(scenarioName, 'About to click search link');
    await I.logInfo(scenarioName, `Waiting for ${testConfig.FindCasesDelay} seconds`);
    await I.wait(testConfig.FindCasesDelay);
    await I.click(searchLinkLocator);
    await I.logInfo(scenarioName, 'Search link clicked, now waiting for case ref input field to be visible and enabled');

    const caseRefLocator = {css: 'input[id="[CASE_REFERENCE]"]'};

    await I.waitForVisible(caseRefLocator);
    await I.waitForEnabled(caseRefLocator);
    await I.logInfo(scenarioName, 'case ref input field now visible and enabled');

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

    await I.logInfo(scenarioName, `waiting for link ${linkLocator.css}`);
    await I.waitForElement(linkLocator);

    // now that waitforNavigation has networkidle2 wait shouldn't need this, but retained for pipeline (autodelay true)
    await I.wait(testConfig.FindCasesDelay);
    await I.waitForNavigationToComplete(linkLocator.css);
    await I.wait(testConfig.CaseworkerCaseNavigateDelay);
};
