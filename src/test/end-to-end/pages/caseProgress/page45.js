'use strict';
const assert = require('assert');

// back to case details after applying
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('a[aria-controls="caseProgressTab"][aria-selected=true]'); 

    // Check text on lhs side is all correct.
    const texts = await I.grabTextFrom('markdown p.govuk-body-s');
    assert (texts.length === 17);

    const linkText = await I.grabTextFrom('span.govuk-details__summary-text');
    assert (linkText === 'View the documents needed by HM Courts and Tribunal Service'); 

    await I.seeNumberOfVisibleElements('p.govuk-body-s a', 0);

    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt=COMPLETED]', 7);
    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="NOT STARTED"]', 0);
    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="IN PROGRESS"]', 1);

    const caseRef = await I.grabTextFrom('h1.heading-h1');

    // sign out
    await I.clickLink('div.proposition-right a');
    return caseRef.replace('#','');
}
